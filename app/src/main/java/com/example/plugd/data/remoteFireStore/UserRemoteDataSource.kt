package com.example.plugd.data.remoteFireStore

import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
class UserRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val userCollection = firestore.collection("users")

    suspend fun uploadUser(user: UserProfileEntity) {
        userCollection.document(user.userId).set(user).await()
    }

    suspend fun fetchAllUsers(): List<UserProfileEntity> {
        val snapshot = userCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(UserProfileEntity::class.java) }
    }

    suspend fun fetchUserById(userId: String): UserProfileEntity? {
        return userCollection.document(userId).get().await()
            .toObject(UserProfileEntity::class.java)
    }
}