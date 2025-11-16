package com.example.plugd.data.localRoom.dao

import androidx.room.*
import com.example.plugd.data.localRoom.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activities WHERE ownerUserId = :userId ORDER BY timestamp DESC")
    fun getActivitiesForUser(userId: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities WHERE ownerUserId = :userId")
    suspend fun deleteActivitiesForUser(userId: String)

    @Transaction
    suspend fun replaceAll(activities: List<ActivityEntity>) {
        val userId = activities.firstOrNull()?.ownerUserId ?: return
        deleteActivitiesForUser(userId)
        insertActivities(activities)
    }
}