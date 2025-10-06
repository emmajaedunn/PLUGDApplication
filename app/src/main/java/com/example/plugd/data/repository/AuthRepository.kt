package com.example.plugd.data.repository

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

        // ✅ Create Firestore user document
        usersCollection.document(firebaseUser.uid)
            .set(userEntity)
            .await()

        // ✅ Cache locally
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

        // Auto-create Firestore profile if missing
        val snapshot = usersCollection.document(firebaseUser.uid).get().await()
        val userEntity = snapshot.toObject(UserEntity::class.java)
            ?: run {
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
    fun logout() = authService.logout()

}















/* working package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.api.LoginRequest
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val api: ApiService,
    private val userDao: UserDao
) {
    private val firestore = FirebaseFirestore.getInstance("users")
    val snapshot = firestore.collection("users")
        .document(firebaseUser.uid)
        .get()
        .await()


    /** 🔹 Register new user via Firebase & backend REST API */
    suspend fun register(
        name: String,
        username: String,
        email: String,
        password: String
    ): UserEntity = withContext(Dispatchers.IO) {
        // 1️⃣ Create Firebase user
        val firebaseUser = authService.registerUser(email, password)
            ?: throw Exception("Firebase registration failed")

        // 2️⃣ Register with backend (if required)
        val body = mapOf(
            "email" to email,
            "password" to password,
            "username" to username
        )
        val apiResponse = api.register(body)
        if (!apiResponse.isSuccessful) {
            throw Exception("Backend registration failed: ${apiResponse.message()}")
        }

        // 3️⃣ Create user entity
        val userEntity = UserEntity(
            userId = firebaseUser.uid,
            name = name,
            username = username,
            email = email,
            password = password
        )

        // 4️⃣ Save to Firestore
        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(userEntity)
            .await()

        // 5️⃣ Cache locally
        userDao.insertUser(userEntity)

        userEntity
    }

    /** 🔹 Login user (Firebase + Backend + Firestore + Room) */
    suspend fun login(email: String, password: String): UserEntity = withContext(Dispatchers.IO) {
        // 1️⃣ Firebase Auth
        val firebaseUser = authService.loginUser(email, password)
            ?: throw Exception("Firebase login failed")

        // 2️⃣ Backend API login
        val apiResponse = api.login(LoginRequest(email, password))
        if (!apiResponse.isSuccessful) {
            throw Exception("Backend login failed: ${apiResponse.message()}")
        }

        val apiUser = apiResponse.body()?.user
        val roleFromBackend = apiUser?.role ?: "User"

        // 3️⃣ Fetch from Firestore
        val snapshot = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        val userEntity = if (snapshot.exists()) {
            snapshot.toObject(UserEntity::class.java)!!.copy(name = apiUser?.name ?: "")
        } else {
            // fallback if Firestore document doesn’t exist
            val newUser = UserEntity(
                userId = firebaseUser.uid,
                username = apiUser?.username ?: "Unknown",
                name = apiUser?.name ?: "",
                email = email,
                password = password
            )
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(newUser)
                .await()
            newUser
        }

        // 4️⃣ Cache locally
        userDao.insertUser(userEntity)
        userEntity
    }

    /** 🔹 Logout user */
    fun logout() {
        authService.logout()
    }
}*/










/*package com.example.plugd.data.repository

import android.util.Log
import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val api: ApiService,
    private val userDao: UserDao
) {
    private val firestore = FirebaseFirestore.getInstance()

    /** Login user */
    suspend fun login(email: String, password: String): UserEntity = withContext(Dispatchers.IO) {
        val firebaseUser = authService.loginUser(email, password)
            ?: throw Exception("Firebase login failed")

        // Firestore profile exists check
        val userEntity = ensureFirestoreProfile(firebaseUser.uid, email)

        // Insert into local Room
        userDao.insertUser(userEntity)
        userEntity
    }

    /** Register new user */
    suspend fun register(name: String, username: String, email: String, password: String): UserEntity =
        withContext(Dispatchers.IO) {
            val firebaseUser = authService.registerUser(email, password)
                ?: throw Exception("Firebase register failed")

            val entity = UserEntity(
                userId = firebaseUser.uid,
                name = name,
                username = username,
                email = email,
                password = password
            )

            // ✅ Create Firestore profile and wait for it
            val profileData = mapOf(
                "userId" to firebaseUser.uid,
                "name" to name,
                "username" to username,
                "email" to email,
                "bio" to "",
                "location" to "",
                "role" to "User",
                "followersCount" to 0,
                "notificationsEnabled" to true,
                "darkModeEnabled" to false,
                "biometricEnabled" to false,
                "pushEnabled" to true,
                "createdAt" to System.currentTimeMillis()
            )
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(firebaseUser.uid)
                .set(profileData)
                .await() // <-- important: wait for Firestore write

            userDao.insertUser(entity)
            entity
        }

    /** Get user from local cache */
    suspend fun getLocalUser(email: String, password: String): UserEntity? =
        userDao.getUserByEmailAndPassword(email, password)

    // ---------------- Firestore helpers ---------------- //

    /** Create Firestore profile */
    private suspend fun createFirestoreProfile(
        uid: String,
        name: String,
        username: String,
        email: String
    ): UserEntity {
        val profileData = mapOf(
            "userId" to uid,
            "name" to name,
            "username" to username,
            "email" to email,
            "bio" to "",
            "location" to "",
            "role" to "User",
            "followersCount" to 0,
            "notificationsEnabled" to true,
            "darkModeEnabled" to false,
            "biometricEnabled" to false,
            "pushEnabled" to true,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(uid).set(profileData).await()

        return UserEntity(
            userId = uid,
            name = name,
            username = username,
            email = email,
            password = "" // you can choose to store hashed password locally if needed
        )
    }

    /** Ensure Firestore profile exists on login */
    private suspend fun ensureFirestoreProfile(uid: String, email: String): UserEntity {
        val doc = firestore.collection("users").document(uid).get().await()
        return if (doc.exists()) {
            val data = doc.data!!
            UserEntity(
                userId = uid,
                name = data["name"] as? String ?: "",
                username = data["username"] as? String ?: "",
                email = data["email"] as? String ?: email,
                password = "" // blank locally
            )
        } else {
            // Create a default profile if missing
            createFirestoreProfile(uid, "", "", email)
        }
    }
}*/





















/* working auth
package com.example.plugd.data.repository


import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.api.LoginRequest
import com.example.plugd.remote.firebase.FirebaseAuthService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val api: ApiService,
    private val userDao: UserDao
) {
    private val firestore = FirebaseFirestore.getInstance()

    // Login user
    suspend fun login(email: String, password: String): UserEntity = withContext(Dispatchers.IO) {
        val firebaseUser = authService.loginUser(email, password)
            ?: throw Exception("Firebase login failed")

        val response = api.login(LoginRequest(email, password))
        if (!response.isSuccessful) throw Exception("API login failed")

        val apiUser = response.body()!!.user

        val entity = UserEntity(
            userId = firebaseUser.uid,
            name = apiUser.name,
            username = apiUser.username,
            email = apiUser.email,
            password = password
        )

        // ✅ Ensure Firestore profile exists
        ensureFirestoreProfile(firebaseUser.uid, entity)

        userDao.insertUser(entity)
        entity
    }

    suspend fun register(name: String, username: String, email: String, password: String): UserEntity =
        withContext(Dispatchers.IO) {
            val firebaseUser = authService.registerUser(email, password)
                ?: throw Exception("Firebase register failed")

            val response = api.register(
                mapOf("name" to name, "username" to username, "email" to email, "password" to password)
            )
            val apiUser = response.body()!!.user

            val entity = UserEntity(
                userId = firebaseUser.uid,
                name = apiUser.name,
                username = apiUser.username,
                email = apiUser.email,
                password = password
            )

            // ✅ Create Firestore profile when registering
            createFirestoreProfile(firebaseUser.uid, entity)

            userDao.insertUser(entity)
            entity
        }

    suspend fun getLocalUser(email: String, password: String): UserEntity? =
        userDao.getUserByEmailAndPassword(email, password)

    // --- Firestore integration helpers ---
    private suspend fun createFirestoreProfile(uid: String, entity: UserEntity) {
        val profileData = mapOf(
            "userId" to uid,
            "name" to entity.name,          // ✅ add this
            "username" to entity.username,
            "email" to entity.email,
            "bio" to "",
            "location" to "",
            "role" to "User",
            "followersCount" to 0,
            "notificationsEnabled" to true,
            "darkModeEnabled" to false,
            "biometricEnabled" to false,
            "pushEnabled" to true,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(uid).set(profileData).await()
    }

    private suspend fun ensureFirestoreProfile(uid: String, entity: UserEntity) {
        val firestore = FirebaseFirestore.getInstance()
        val docRef = firestore.collection("users").document(uid).get().await()
        if (!docRef.exists()) {
            val profileData = mapOf(
                "userId" to uid,
                "username" to entity.username,
                "email" to entity.email,
                "bio" to "",
                "location" to "",
                "role" to "Artist",
                "biometricEnabled" to false,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(profileData).await()
        }
    }
}




















/*package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.api.LoginRequest
import com.example.plugd.remote.firebase.FirebaseAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val api: ApiService,
    private val userDao: UserDao
) {
    suspend fun login(email: String, password: String): UserEntity = withContext(Dispatchers.IO) {
        val firebaseUser = authService.loginUser(email, password)
            ?: throw Exception("Firebase login failed")

        val response = api.login(LoginRequest(email, password))
        if (!response.isSuccessful) throw Exception("API login failed")

        val apiUser = response.body()!!.user
        val entity = UserEntity(
            userId = firebaseUser.uid,
            name = apiUser.name,
            username = apiUser.username,
            email = apiUser.email,
            password = password
        )

        userDao.insertUser(entity)
        entity
    }

    suspend fun register(name: String, username: String, email: String, password: String): UserEntity =
        withContext(Dispatchers.IO) {
            val firebaseUser = authService.registerUser(email, password)
                ?: throw Exception("Firebase register failed")

            val response = api.register(
                mapOf("name" to name, "username" to username, "email" to email, "password" to password)
            )
            val apiUser = response.body()!!.user

            val entity = UserEntity(
                userId = firebaseUser.uid,
                name = apiUser.name,
                username = apiUser.username,
                email = apiUser.email,
                password = password
            )

            userDao.insertUser(entity)
            entity
        }

    suspend fun getLocalUser(email: String, password: String): UserEntity? =
        userDao.getUserByEmailAndPassword(email, password)
}*/
*/