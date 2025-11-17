package com.example.plugd.data.remoteFireStore

import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
class UserRemoteDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // User collection reference
    private val userCollection = firestore.collection("users")

    // Upload user to Firestore
    suspend fun uploadUser(user: UserProfileEntity) {
        userCollection.document(user.userId).set(user).await()
    }

    // Fetch all users from Firestore
    suspend fun fetchAllUsers(): List<UserProfileEntity> {
        val snapshot = userCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(UserProfileEntity::class.java) }
    }

    // Fetch a user by ID from Firestore
    suspend fun fetchUserById(userId: String): UserProfileEntity? {
        return userCollection.document(userId).get().await()
            .toObject(UserProfileEntity::class.java)
    }
}