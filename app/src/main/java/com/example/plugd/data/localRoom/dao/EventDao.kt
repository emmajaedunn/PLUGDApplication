package com.example.plugd.data.localRoom.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.plugd.data.localRoom.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    // Insert a single event
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    // Insert multiple events
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    // Fetch all events
    @Query("SELECT * FROM events")
    fun getAllEvents(): Flow<List<EventEntity>>

    // Update an event
    @Update
    suspend fun updateEvent(event: EventEntity)

    // Delete all events
    @Query("DELETE FROM events")
    suspend fun clearEvents()

    // Delete an event by ID
    @Query("DELETE FROM events WHERE eventId = :eventId")
    suspend fun deleteEventById(eventId: String)
}