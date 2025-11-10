package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.EventDao
import com.example.plugd.data.localRoom.entity.EventEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val firestore: FirebaseFirestore
) {
    private val eventsCollection = firestore.collection("events")

    val events: Flow<List<EventEntity>> = eventDao.getAllEvents()

    suspend fun addEvent(event: EventEntity) {
        // Save to Firestore
        eventsCollection.document(event.eventId).set(event).await()
        // Cache in Room
        eventDao.insertEvent(event)
    }

    suspend fun refreshEvents() {
        val snapshot = eventsCollection.get().await()
        val firestoreEvents = snapshot.toObjects(EventEntity::class.java)
        // Clear local cache and insert the fresh data
        eventDao.clearEvents()
        eventDao.insertAll(firestoreEvents)
    }

    suspend fun updateEvent(event: EventEntity) {
        // Update in the local Room database
        eventDao.updateEvent(event)
        // Update in the remote Firestore database
        firestore.collection("events").document(event.eventId).set(event).await()
    }

    // --- THIS IS THE NEW FUNCTION ---
    // It deletes the event from both Firestore and the local Room DB
    suspend fun deleteEvent(eventId: String) {
        // 1. Delete from the remote Firestore database
        eventsCollection.document(eventId).delete().await()

        // 2. Delete from the local Room database using the new DAO function
        eventDao.deleteEventById(eventId)
    }
}





















/*import com.example.plugd.data.localRoom.dao.EventDao
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.remoteFireStore.EventRemoteDataSource
import com.example.plugd.remote.api.ApiService
import com.example.plugd.remote.api.RetrofitInstance.api
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val eventDao: EventDao,
    private val eventRemote: EventRemoteDataSource,
    private val apiService: ApiService        // JUST ADDED Add ApiService
) {

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Offline-first flow (UI reads from Room)
    val events: Flow<List<EventEntity>> = eventDao.getAllEvents()

    // Create via REST API, then cache
    suspend fun addEvent(event: EventEntity): EventEntity {
        // Send only what server needs (ignore client-generated eventId)
        val resp = api.addEvent(event.copy(eventId = "")) // or .let { strip id in a mapper }
        if (!resp.isSuccessful) error("API ${resp.code()}: ${resp.errorBody()?.string()}")
        val created = resp.body()!!
        eventDao.insertEvent(created) // cache to Room
        return created
    }

    // Load via REST API, then cache
    suspend fun refreshEvents(): List<EventEntity> {
        val resp = api.listEvents()
        if (!resp.isSuccessful) error("API ${resp.code()}: ${resp.errorBody()?.string()}")
        val list = resp.body().orEmpty()
        eventDao.clearEvents()
        eventDao.insertAll(list)
        return list
    }
}

    /* Add new event -> Room + API
    suspend fun addEvent(event: EventEntity) {
        eventDao.insertEvent(event)      // save locally first
        try {
            eventRemote.addEvent(event)  // push to API
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // JUST ADDED
        try {
            // Push to REST API
            apiService.addEvent(event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    // Load events from Firestore -> Room
    suspend fun loadEvents() {
        try {
            val remoteEvents = eventRemote.getEvents()
            eventDao.clearEvents()
            eventDao.insertAll(remoteEvents)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Load events from REST API -> Room
    suspend fun loadEventsFromApi() {
        try {
            val userId = currentUserId ?: return
            val response = apiService.getUserEvents(userId) // Response<List<EventEntity>>
            if (response.isSuccessful) {
                val remoteEvents = response.body() ?: emptyList()
                eventDao.clearEvents()
                eventDao.insertAll(remoteEvents)
            } else {
                // Handle API error
                println("API error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncEvents() {
        try {
            val userId = currentUserId ?: return
            val response = apiService.getUserEvents(userId) // Response<List<EventEntity>>
            if (response.isSuccessful) {
                val remoteEvents = response.body() ?: emptyList()
                eventDao.clearEvents()
                eventDao.insertAll(remoteEvents)
            } else {
                println("API error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}*/