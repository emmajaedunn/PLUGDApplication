package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.ActivityDao
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ActivityRepository(
    private val activityDao: ActivityDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getActivitiesForUser(userId: String): Flow<List<ActivityEntity>> = callbackFlow {
        // Cache in Room first
        launch(Dispatchers.IO) {
            val cached = activityDao.getActivitiesForUser(userId).first()
            trySend(cached)
        }

        // Real-time updates
        val ref = firestore.collection("activities")
            .document(userId)
            .collection("feed")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        // Listen for changes
        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            // Convert to ActivityEntity
            val list = snapshot.documents.mapNotNull { doc ->
                val type = doc.getString("type") ?: return@mapNotNull null
                val fromUserId = doc.getString("fromUserId") ?: return@mapNotNull null
                val fromUsername = doc.getString("fromUsername") ?: "Someone"
                val message = doc.getString("message") ?: ""
                val postId = doc.getString("postId")
                val timestamp = doc.getLong("timestamp") ?: 0L

                ActivityEntity(
                    id = doc.id,
                    ownerUserId = userId,
                    fromUserId = fromUserId,
                    fromUsername = fromUsername,
                    message = message,
                    postId = postId,
                    type = type,
                    timestamp = timestamp
                )
            }

            trySend(list)

            // Cache in Room
            launch(Dispatchers.IO) {
                activityDao.replaceAll(list)
            }
        }

        // Cancel listener when the flow is cancelled
        awaitClose { listener.remove() }
    }
}

