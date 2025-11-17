package com.example.plugd.data.localRoom.dao

import androidx.room.*
import com.example.plugd.data.localRoom.entity.ChannelEntity
import com.example.plugd.data.localRoom.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // Fetch all channels
    @Query("SELECT * FROM channels")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    // Fetches channel by ID
    @Query("SELECT * FROM channels WHERE channelId = :channelId")
    suspend fun getChannelById(channelId: String): ChannelEntity

    // Get messages for a channel
    @Query("SELECT * FROM messages WHERE channelId = :channelId")
    fun getMessages(channelId: String): Flow<List<MessageEntity>>

    // Insert channel
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // Insert channels
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    // Delete messages for a channel
    @Query("DELETE FROM messages WHERE channelId = :channelId")
    suspend fun deleteMessages(channelId: String)
}