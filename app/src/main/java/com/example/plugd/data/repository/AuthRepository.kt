package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val userDao: UserDao, // local cache
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    /** Register user + create Firestore profile */
    suspend fun register(name: String, username: String, email: String, password: String): UserEntity {
        val firebaseUser = authService.registerUser(email, password)
            ?: throw Exception("Firebase registration failed")

        // Create a simple map for the Firestore document
        val userProfileMap = hashMapOf(
            "userId" to firebaseUser.uid,
            "name" to name,
            "username" to username,
            "email" to email,
            "bio" to "",
            "location" to "",
            "followers" to emptyList<String>(),
            "following" to emptyList<String>(),
            "events" to emptyList<String>(),
            "profilePictureUrl" to null
        )

        // Create Firestore user document from the map
        usersCollection.document(firebaseUser.uid)
            .set(userProfileMap)
            .await()

        // Create a local entity and cache it
        val userEntity = UserEntity(
            userId = firebaseUser.uid,
            name = name,
            username = username,
            email = email
        )
        userDao.insertUser(userEntity)
        return userEntity
    }

    /** Login user + fetch Firestore profile */
    suspend fun login(email: String, password: String): UserEntity {
        val firebaseUser = authService.loginUser(email, password)
            ?: throw Exception("Firebase login failed")

        val snapshot = usersCollection.document(firebaseUser.uid).get().await()
        val userEntity = snapshot.toObject(UserEntity::class.java)
            ?: throw Exception("User not found in Firestore")

        userDao.insertUser(userEntity)
        return userEntity
    }

    suspend fun loginWithCredential(credential: AuthCredential): UserEntity {
        val firebaseUser = authService.loginWithCredential(credential)
            ?: throw Exception("Firebase login failed")

        val snapshot = usersCollection.document(firebaseUser.uid).get().await()
        val userEntity = snapshot.toObject(UserEntity::class.java) ?: run {
            val newUser = UserEntity(
                userId = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                username = firebaseUser.displayName?.replace(" ", "") ?: "",
                email = firebaseUser.email ?: ""
            )
            usersCollection.document(firebaseUser.uid).set(newUser).await()
            newUser
        }

        userDao.insertUser(userEntity)
        return userEntity
    }

    suspend fun logout(profileRepository: ProfileRepository? = null) {
        authService.logout()
        profileRepository?.clearLocalCache()
    }
}




















/*package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val userDao: UserDao, // local cache
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    /* Register user + create Firestore profile */
    suspend fun register(name: String, username: String, email: String, password: String): UserEntity {
        val firebaseUser = authService.registerUser(email, password)
            ?: throw Exception("Firebase registration failed")

        val userEntity = UserEntity(
            userId = firebaseUser.uid,
            name = name,
            username = username,
            email = email,
            password = password
        )

        // Create Firestore user document
        usersCollection.document(firebaseUser.uid)
            .set(userEntity)
            .await()

        // Cache locally
        userDao.insertUser(userEntity)
        return userEntity
    }

    /** Login user + fetch Firestore profile */
    suspend fun login(email: String, password: String): UserEntity {
        val firebaseUser = authService.loginUser(email, password)
            ?: throw Exception("Firebase login failed")

        val snapshot = usersCollection.document(firebaseUser.uid).get().await()
        val userEntity = snapshot.toObject(UserEntity::class.java)
            ?: throw Exception("User not found in Firestore")

        userDao.insertUser(userEntity)
        return userEntity
    }

    suspend fun loginWithCredential(credential: AuthCredential): UserEntity {
        val firebaseUser = authService.loginWithCredential(credential)
            ?: throw Exception("Firebase login failed")

        val snapshot = usersCollection.document(firebaseUser.uid).get().await()
        val userEntity = snapshot.toObject(UserEntity::class.java) ?: run {
            val newUser = UserEntity(
                userId = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                username = firebaseUser.displayName?.replace(" ", "") ?: "",
                email = firebaseUser.email ?: "",
                password = ""
            )
            usersCollection.document(firebaseUser.uid).set(newUser).await()
            newUser
        }

        userDao.insertUser(userEntity)
        return userEntity
    }

    suspend fun logout(profileRepository: ProfileRepository? = null) {
        authService.logout()
        profileRepository?.clearLocalCache()
    }
}*/