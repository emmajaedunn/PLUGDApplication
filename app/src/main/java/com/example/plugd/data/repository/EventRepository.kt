package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.EventDao
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.remoteFireStore.EventRemoteDataSource
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val eventDao: EventDao,
    private val eventRemote: EventRemoteDataSource
) {
    val events: Flow<List<EventEntity>> = eventDao.getAllEvents()

    suspend fun addEvent(event: EventEntity) {
        eventDao.insertEvent(event)             // save locally
        eventRemote.addEvent(event)             // push to Firestore
    }

    suspend fun loadEvents() {
        val remoteEvents = eventRemote.getEvents()
        eventDao.insertAll(remoteEvents)        // sync Firestore down to Room
    }
}