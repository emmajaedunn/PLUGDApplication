package com.example.plugd.ui.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {

    // Get current user ID
    fun currentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    // Get user document reference
    fun getUserDocument(userId: String? = currentUserId()): DocumentReference {
        val safeId = userId ?: throw IllegalStateException("User not logged in")
        return FirebaseFirestore.getInstance().collection("users").document(safeId)
    }
}
