package com.example.plugd.data.localRoom.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.plugd.data.localRoom.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    // Profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    // Fetch profile by ID
    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileById(userId: String): UserProfileEntity?

    // Clear all profiles
    @Query("DELETE FROM user_profiles")
    suspend fun clearAllProfiles()

    // Clear table
    @Query("DELETE FROM user_profiles")
    suspend fun clearTable()
}