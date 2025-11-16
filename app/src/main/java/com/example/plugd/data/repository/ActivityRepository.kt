package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.ActivityDao
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivityRepository(
    private val activityDao: ActivityDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getActivitiesForUser(userId: String): Flow<List<ActivityEntity>> = callbackFlow {
        // 1️⃣ Emit cached Room data once for THIS user
        launch(Dispatchers.IO) {
            val cached = activityDao.getActivitiesForUser(userId).first()
            trySend(cached)
        }

        // 2️⃣ Live Firestore listener for THIS user
        val ref = firestore.collection("activities")
            .document(userId)
            .collection("feed")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // ViewModel .catch{} will see this
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val list = snapshot.documents.mapNotNull { doc ->
                val type = doc.getString("type") ?: return@mapNotNull null
                val fromUserId = doc.getString("fromUserId") ?: return@mapNotNull null
                val fromUsername = doc.getString("fromUsername") ?: "Someone"
                val message = doc.getString("message") ?: ""
                val postId = doc.getString("postId")
                val timestamp = doc.getLong("timestamp") ?: 0L

                ActivityEntity(
                    id = doc.id,
                    ownerUserId = userId,        // 👈 owner of this feed
                    fromUserId = fromUserId,
                    fromUsername = fromUsername,
                    message = message,
                    postId = postId,
                    type = type,
                    timestamp = timestamp
                )
            }

            trySend(list)

            // cache in Room, only for this owner
            launch(Dispatchers.IO) {
                activityDao.replaceAll(list)
            }
        }

        awaitClose { listener.remove() }
    }
}















/*package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.ActivityDao
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActivityRepository(
    private val activityDao: ActivityDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val auth get() = FirebaseAuth.getInstance()
    private val currentUserId get() = auth.currentUser?.uid

    /**
     * Observes the activity feed for the currently logged-in user in real-time.
     *
     * This function uses a callbackFlow to provide live updates from Firestore.
     * It listens for any changes in the user's "feed" collection and pushes the
     * updated list of activities to the flow.
     */
    fun getActivities(): Flow<List<ActivityEntity>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            trySend(emptyList()) // Send an empty list if no user is logged in
            close()
            return@callbackFlow
        }

        val collection = firestore.collection("activities")
            .document(userId)
            .collection("feed")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Pass the error to the collector
                close(error)
                return@addSnapshotListener
            }

            val activities = snapshot?.toObjects(ActivityEntity::class.java) ?: emptyList()
            trySend(activities) // Push the real-time list to the flow
        }

        // This will be called when the flow is cancelled or closed
        awaitClose { listener.remove() }
    }

    /**
     * One-shot refresh from Firestore. This can be used to manually trigger an update,
     * but getActivities() is preferred for real-time updates.
     */
    suspend fun refreshActivities() {
        val userId = currentUserId ?: return
        try {
            val snapshot = firestore.collection("activities")
                .document(userId)
                .collection("feed")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()

            val activities = snapshot.toObjects(ActivityEntity::class.java)
            // You might want to update a local cache (e.g., Room) here if you have one
            // For example: activityDao.insertAll(activities)
        } catch (e: Exception) {
            // Handle exceptions, e.g., logging
            println("Error refreshing activities: ${e.message}")
        }
    }
}*/
