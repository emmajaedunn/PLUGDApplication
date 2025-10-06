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
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginWithCredential(credential: AuthCredential): FirebaseUser? {
        return try {
            val result = auth.signInWithCredential(credential).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}


/* suspend fun signInWithGoogle(idToken: String): UserEntity {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val firebaseUser = authService.loginWithCredential(credential)
        ?: throw Exception("Google Sign-In failed")

    // Create Firestore profile if new
    val snapshot = firestore.collection("users").document(firebaseUser.uid).get().await()
    val userEntity = if (snapshot.exists()) {
        snapshot.toObject(UserEntity::class.java)!!
    } else {
        val newUser = UserEntity(
            userId = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            username = firebaseUser.displayName?.replace(" ", "") ?: "",
            email = firebaseUser.email ?: "",
            password = ""
        )
        firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
        newUser
    }

    userDao.insertUser(userEntity)
    return userEntity
}

 */











/* working
package com.example.plugd.remote.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }


    // JUST ADDED
    suspend fun loginWithCredential(credential: AuthCredential) = try {
        val result = auth.signInWithCredential(credential).await()
        result.user  // This is the FirebaseUser
    } catch (e: Exception) {
        null
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun reauthenticateUser(email: String, password: String){
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(email, password)
        user?.reauthenticate(credential)?.await()
    }

    suspend fun updatePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)?.await()
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}*/