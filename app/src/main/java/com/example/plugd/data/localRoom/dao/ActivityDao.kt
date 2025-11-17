package com.example.plugd.data.localRoom.dao

import androidx.room.*
import com.example.plugd.data.localRoom.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    // Get activities for a user
    @Query("SELECT * FROM activities WHERE ownerUserId = :userId ORDER BY timestamp DESC")
    fun getActivitiesForUser(userId: String): Flow<List<ActivityEntity>>

    // Insert activities
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    // Delete activities for a user
    @Query("DELETE FROM activities WHERE ownerUserId = :userId")
    suspend fun deleteActivitiesForUser(userId: String)

    // Replace all activities for a user
    @Transaction
    suspend fun replaceAll(activities: List<ActivityEntity>) {
        val userId = activities.firstOrNull()?.ownerUserId ?: return
        deleteActivitiesForUser(userId)
        insertActivities(activities)
    }
}