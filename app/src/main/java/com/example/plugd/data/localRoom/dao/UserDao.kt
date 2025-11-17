package com.example.plugd.data.localRoom.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.plugd.data.localRoom.entity.UserEntity

@Dao
interface UserDao {

    // Insert a user into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Fetch a user by email and password
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    // Fetch a user
    @Query("SELECT * FROM users ORDER BY userId DESC LIMIT 1")
    suspend fun getLastLoggedInUser(): UserEntity?

    // Fetch a user by ID
    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?
}