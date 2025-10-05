package com.example.plugd.data.repository

import android.util.Log
import com.example.plugd.data.localRoom.dao.UserProfileDao
import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.example.plugd.data.mappers.toUserProfile
import com.example.plugd.data.mappers.toUserProfileEntity
import com.example.plugd.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val profileDao: UserProfileDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    /** Listen for live updates from Firestore */
    fun observeRemoteProfile(userId: String, onProfileChanged: (UserProfile?) -> Unit) {
        usersCollection.document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen failed", error)
                    onProfileChanged(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(UserProfileEntity::class.java)?.toUserProfile()
                    onProfileChanged(user)

                    // Update local Room cache
                    user?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            profileDao.insertProfile(it.toUserProfileEntity())
                        }
                    }
                } else {
                    onProfileChanged(null)
                }
            }
    }

    /** Update a single field in Firestore and local cache */
    suspend fun updateProfileField(userId: String, field: String, value: String) {
        val userRef = usersCollection.document(userId)
        userRef.update(field, value).await()

        val localProfile = profileDao.getProfileById(userId)
        localProfile?.let { profile ->
            val updated = when (field) {
                "username" -> profile.copy(username = value)
                "email" -> profile.copy(email = value)
                "bio" -> profile.copy(bio = value)
                "location" -> profile.copy(location = value)
                else -> profile
            }
            profileDao.insertProfile(updated)
        }
    }

    /** Get cached local profile */
    suspend fun getLocalProfile(userId: String): UserProfileEntity? =
        profileDao.getProfileById(userId)

    /** Fetch latest Firestore profile once */
    suspend fun getRemoteProfile(userId: String): UserProfile? {
        val snapshot = usersCollection.document(userId).get().await()
        val entity = snapshot.toObject(UserProfileEntity::class.java)
        // Update local cache
        entity?.let {
            CoroutineScope(Dispatchers.IO).launch {
                profileDao.insertProfile(it)
            }
        }
        return entity?.toUserProfile()
    }
}



















/* working profile auth

package com.example.plugd.repository


import android.util.Log
import com.example.plugd.data.localRoom.dao.UserProfileDao
import com.example.plugd.data.localRoom.entity.UserProfileEntity
import com.example.plugd.data.mappers.toUserProfileEntity
import com.example.plugd.data.mappers.toUserProfile
import com.example.plugd.data.remoteFireStore.UserRemoteDataSource
import com.example.plugd.model.UserProfile
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.api.RetrofitInstance.api
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val profileDao: UserProfileDao,
    private val remote: UserRemoteDataSource
) {
    // Get profile from Room (offline cache)
    suspend fun getLocalProfile(userId: String): UserProfileEntity? =
        profileDao.getProfileById(userId)

    suspend fun fetchRemoteProfile(userId: String): UserProfileEntity? {
        val remoteUser = remote.getUser(userId)  // <-- Firestore fetch
        return remoteUser?.toUserProfileEntity()?.also {
            profileDao.insertProfile(it)
        }
    }


    /* Fetch profile from API (remote source of truth)
    suspend fun fetchRemoteProfile(userId: String): UserProfileEntity? {
        val response = api.getProfile(userId)
        return if (response.isSuccessful) {
            response.body()?.let { dto ->
                val entity = dto.toUserProfileEntity()
                profileDao.insertProfile(entity)
                entity
            }
        } else null
    }*/

    fun observeRemoteProfile(userId: String, onProfileChanged: (UserProfile?) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen failed", error)
                    onProfileChanged(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val data = snapshot.data
                        val user = UserProfile(
                            userId = data?.get("userId") as? String ?: "",
                            name = data?.get("name") as? String ?: "",
                            username = data?.get("username") as? String ?: "",
                            email = data?.get("email") as? String ?: "",
                            phone = data?.get("phone") as? String,
                            role = data?.get("role") as? String ?: "User",
                            bio = data?.get("bio") as? String,
                            gender = data?.get("gender") as? String,
                            location = data?.get("location") as? String,
                            followersCount = (data?.get("followersCount") as? Long)?.toInt() ?: 0,
                            events = emptyList(),
                            notificationsEnabled = data?.get("notificationsEnabled") as? Boolean ?: true,
                            darkModeEnabled = data?.get("darkModeEnabled") as? Boolean ?: false,
                            biometricEnabled = data?.get("biometricEnabled") as? Boolean ?: false,
                            pushEnabled = data?.get("pushEnabled") as? Boolean ?: true
                        )
                        onProfileChanged(user)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Failed to map user profile", e)
                        onProfileChanged(null)
                    }
                } else {
                    onProfileChanged(null)
                }
            }
    }

    suspend fun updateProfileField(userId: String, field: String, value: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.update(field, value).await()

        // Update local cache (optional)
        val localProfile = profileDao.getProfileById(userId)
        localProfile?.let { profile ->
            val updated = when (field) {
                "username" -> profile.copy(username = value)
                "email" -> profile.copy(email = value)
                "bio" -> profile.copy(bio = value)
                "location" -> profile.copy(location = value)
                else -> profile
            }
            profileDao.insertProfile(updated)
        }
    }
}
*/

















/*class ProfileRepository(
    private val profileDao: UserProfileDao,
    private val remoteFirestore: UserRemoteDataSource
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentProfile = MutableStateFlow<UserProfileEntity?>(null)
    val currentProfile: StateFlow<UserProfileEntity?> = _currentProfile

    suspend fun getLocalProfile(userId: String): UserProfileEntity? {
        val local = profileDao.getProfileById(userId)
        _currentProfile.value = local
        return local
    }

    suspend fun fetchRemoteProfile(userId: String): UserProfileEntity? {
        val remoteUser = remoteFirestore.getUser(userId) ?: return null
        val entity = remoteUser.toUserProfileEntity()
        profileDao.insertProfile(entity)
        _currentProfile.value = entity
        return entity
    }

    fun observeRemoteProfile(userId: String) {
        remoteFirestore.observeUser(userId) { remoteUser ->
            remoteUser?.let {
                val entity = it.toUserProfileEntity()
                scope.launch {
                    profileDao.insertProfile(entity)
                    _currentProfile.value = entity
                }
            }
        }
    }

    suspend fun updateProfileField(userId: String, field: String, value: Any) {
        val current = _currentProfile.value ?: profileDao.getProfileById(userId)
        current?.let { profile ->
            val updated = when (field) {
                "username" -> profile.copy(username = value as String)
                "email" -> profile.copy(username = value as String) // optional if email stored separately
                "phone" -> profile.copy(phone = value as String)
                "location" -> profile.copy(location = value as String)
                "darkModeEnabled" -> profile.copy(darkModeEnabled = value as Boolean)
                "biometricEnabled" -> profile.copy(biometricEnabled = value as Boolean)
                else -> profile
            }
            profileDao.insertProfile(updated)
            remoteFirestore.updateUser(updated.toUserProfile())
            _currentProfile.value = updated
        }
    }
}
*/












/*package com.example.plugd.repository

import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.remoteFireStore.UserRemoteDataSource
import com.example.plugd.data.mappers.toUserEntity
import com.example.plugd.data.mappers.toUserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileRepository(
    private val userDao: UserDao,
    private val remoteFirestore: UserRemoteDataSource
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current user state
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    suspend fun getCurrentUser(): UserEntity? {
        val local = userDao.getLastLoggedInUser()
        _currentUser.value = local
        return local
    }

    suspend fun loadUserFromRoom(userId: String): UserEntity? {
        val local = userDao.getUserById(userId)
        _currentUser.value = local
        return local
    }

    suspend fun fetchUserFromRemote(userId: String): UserEntity? {
        val remoteUser = remoteFirestore.getUser(userId) ?: return null
        val entity = remoteUser.toUserEntity() // mapping
        userDao.insertUser(entity)
        _currentUser.value = entity
        return entity
    }

    fun observeUser(userId: String) {
        remoteFirestore.observeUser(userId) { userProfile ->
            userProfile?.let {
                val entity = it.toUserEntity()
                scope.launch {
                    userDao.insertUser(entity)
                    _currentUser.value = entity
                }
            }
        }
    }

    suspend fun updateUserProfile(user: UserEntity): UserEntity {
        userDao.insertUser(user)
        remoteFirestore.updateUser(user.toUserProfile())
        _currentUser.value = user
        return user
    }
}











/*package com.example.plugd.repository

import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.remoteFireStore.UserRemoteDataSource
import com.example.plugd.data.mappers.toUserEntity
import com.example.plugd.data.mappers.toUserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserRepository(
    private val userDao: UserDao,
    private val remote: UserRemoteDataSource
) {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getCurrentUser(): UserEntity? {
        val local = userDao.getLastLoggedInUser()
        if (local != null) _currentUser.value = local
        return local
    }

    suspend fun getUserById(userId: String): UserEntity? {
        // Try to get from local DB first
        val localUser = userDao.getUserById(userId)
        if (localUser != null) return localUser

        // Fetch from remote
        val remoteUserProfile = remote.getUser(userId) // suspend function
        val remoteEntity = remoteUserProfile?.toUserEntity() // convert to UserEntity
        remoteEntity?.let {
            userDao.insertUser(it) // save to Room
        }
        return remoteEntity
    }

   /* suspend fun getUserById(uid: String): UserEntity? {
        return userDao.getUserById(uid)
    }*/

    /**
     * 🔹 Fetch latest user from Firestore and save to Room
     */
    suspend fun fetchUserFromRemote(userId: String): UserEntity? {
        return try {
            val remoteUser = remote.getUser(userId)
            remoteUser?.let {
                val entity = it.toUserEntity()
                userDao.insertUser(entity) // save locally
                _currentUser.value = entity
                entity
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun loadUserFromRoom(userId: String): UserEntity? {
        val local = userDao.getUserById(userId)
        _currentUser.value = local
        return local
    }

    /**
     * 🔹 Listen for live updates from Firestore and keep Room in sync
     */
    fun observeUser(userId: String) {
        remote.observeUser(userId) { remoteUser ->
            remoteUser?.let {
                val entity = it.toUserEntity()
                scope.launch {
                    userDao.insertUser(entity)
                    _currentUser.value = entity
                }
            }
        }
    }

    /**
     * 🔹 Update user both locally and remotely
     */
    suspend fun updateUserProfile(user: UserEntity): UserEntity {
        userDao.insertUser(user) // update Room
        remote.updateUser(user.toUserProfile()) // update Firestore
        _currentUser.value = user
        return user
    }

    suspend fun logout() {
        _currentUser.value = null
    }
}*/