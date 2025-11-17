package com.example.plugd.data.repository

import android.util.Log
import com.example.plugd.data.localRoom.dao.EventDao
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.RetrofitInstance
import com.example.plugd.remote.api.dto.CreateEventDto
import com.google.firebase.auth.FirebaseAuth
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
    private val usersCollection = firestore.collection("users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val events: Flow<List<EventEntity>> = eventDao.getAllEvents()

    // Create event using REAL name + username from Firestore.
    suspend fun createEvent(
        name: String,
        category: String?,
        description: String?,
        location: String?,
        latitude: Double? = null,
        longitude: Double? = null,
        date: Long,
        spotifyPlaylistId: String? = null,
        supportDocs: String? = null
    ) {
        val user = auth.currentUser ?: throw Exception("Not logged in")
        val uid = user.uid

        // Get full name + username for UI
        val userSnap = usersCollection.document(uid).get().await()
        val fullName = userSnap.getString("name") ?: ""
        val username = userSnap.getString("username") ?: ""

        // DTO for REST API
        val dto = CreateEventDto(
            name = name,
            category = category,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude,
            date = date,
            spotifyPlaylistId = spotifyPlaylistId
        )

        val response = RetrofitInstance.api.addEvent(dto)
        if (!response.isSuccessful) {
            throw Exception("Failed to create event via API: ${response.code()} ${response.message()}")
        }

        val remoteEvent = response.body()
            ?: throw Exception("Empty event returned by API")

        // Ensure owner info + supportDocs are set for Room
        val eventForRoom = remoteEvent.copy(
            createdBy = uid,
            createdByName = fullName,
            createdByUsername = username,
            ownerUid = remoteEvent.ownerUid?.takeIf { it.isNotBlank() } ?: uid,
            supportDocs = supportDocs ?: remoteEvent.supportDocs
        )

        // Cache in Room → offline support
        eventDao.insertEvent(eventForRoom)
    }

    // Refresh events from API
    suspend fun refreshEvents() {
        try {
            val response = RetrofitInstance.api.listEvents()

            if (response.isSuccessful) {
                val remoteEvents = response.body().orEmpty()

                // Replace local cache with latest from API
                eventDao.clearEvents()
                eventDao.insertAll(remoteEvents)
            } else {
                Log.e(
                    "EventRepository",
                    "refreshEvents API error: ${response.code()} ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("EventRepository", "refreshEvents exception", e)
        }
    }

    // Add event to Firestore
    suspend fun addEvent(event: EventEntity) {
        eventsCollection.document(event.eventId).set(event).await()
        eventDao.insertEvent(event)
    }

    // Update event in Firestore
    suspend fun updateEvent(event: EventEntity) {
        eventDao.updateEvent(event)
        firestore.collection("events").document(event.eventId).set(event).await()
    }

    // Delete event from Firestore
    suspend fun deleteEvent(eventId: String) {
        eventsCollection.document(eventId).delete().await()
        eventDao.deleteEventById(eventId)
    }
}
