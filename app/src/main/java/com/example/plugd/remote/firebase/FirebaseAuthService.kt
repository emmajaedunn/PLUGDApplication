package com.example.plugd.remote.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (_: Exception) {
            null
        }
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (_: Exception) {
            null
        }
    }

    suspend fun loginWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            val result = auth.signInWithCredential(credential).await()
            result.user
        } catch (_: Exception) {
            null
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}