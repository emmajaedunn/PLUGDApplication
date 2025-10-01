package com.example.plugd.data.remoteFireStore

import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.ApiService

class EventRemoteDataSource(private val api: ApiService) {
    suspend fun getEvents(): List<EventEntity> = api.getEvents()
    suspend fun addEvent(event: EventEntity) = api.addEvent(event)
}