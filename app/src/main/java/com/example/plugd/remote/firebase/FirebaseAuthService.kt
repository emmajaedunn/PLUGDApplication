package com.example.plugd.remote.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Register a new user with email and password
    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (_: Exception) {
            null
        }
    }

    // Login user with email and password
    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (_: Exception) {
            null
        }
    }

    // Login user with Google credentials
    suspend fun loginWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            val result = auth.signInWithCredential(credential).await()
            result.user
        } catch (_: Exception) {
            null
        }
    }

    // Logout the current user
    fun logout() {
        auth.signOut()
    }
}