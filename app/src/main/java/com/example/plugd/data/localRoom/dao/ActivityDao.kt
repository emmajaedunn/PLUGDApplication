package com.example.plugd.data.localRoom.dao

import androidx.room.*
import com.example.plugd.data.localRoom.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY timestamp DESC")
    fun getActivitiesForUser(userId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities WHERE userId = :userId")
    suspend fun deleteActivitiesForUser(userId: String)

    /**
     * Atomically deletes all activities for a user and inserts a new list.
     */
    @Transaction
    suspend fun replaceAll(activities: List<ActivityEntity>) {
        val userId = activities.firstOrNull()?.userId
        if (userId != null) {
            deleteActivitiesForUser(userId)
            insertActivities(activities)
        }
    }
}