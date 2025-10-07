package com.example.plugd.data.remoteFireStore

import com.example.plugd.data.localRoom.entity.EventEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRemoteDataSource(private val firestore: FirebaseFirestore) {
    private val eventsCol = firestore.collection("events")

    suspend fun addEvent(event: EventEntity) {
        eventsCol.document(event.eventId).set(event)
    }

    suspend fun getEvents(): List<EventEntity> {
        val snapshot = eventsCol.get().await()
        return snapshot.documents.mapNotNull { it.toObject(EventEntity::class.java) }
    }

    suspend fun getUserEvents(userId: String): List<EventEntity> {
        val snapshot = eventsCol
            .whereEqualTo("createdBy", userId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(EventEntity::class.java) }
    }
}








/*package com.example.plugd.data.remoteFireStore

import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.ApiService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRemoteDataSource(
    private val firestore: FirebaseFirestore
    private val api: ApiService
) {

    // Add event to Firestore (both top-level and under user)
    suspend fun addEvent(event: EventEntity) {
        // --- Top-level collection ---
        firestore.collection("events")
            .document(event.eventId)
            .set(event)
            .await()

        // --- User subcollection ---
        firestore.collection("users")
            .document(event.createdBy)
            .collection("events")
            .document(event.eventId)
            .set(event)
            .await()
    }

    // Get events for a specific user
    suspend fun getUserEvents(userId: String): List<EventEntity> {
        return firestore.collection("users")
            .document(userId)
            .collection("events")
            .get()
            .await()
            .toObjects(EventEntity::class.java)
    }

    // Get all events (top-level)
    suspend fun getAllEvents(): List<EventEntity> {
        return firestore.collection("events")
            .get()
            .await()
            .toObjects(EventEntity::class.java)
    }
}*/










/*package com.example.plugd.data.remoteFireStore

import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.remote.api.ApiService

class EventRemoteDataSource(private val api: ApiService) {
    suspend fun getEvents(): List<EventEntity> = api.getEvents()
    suspend fun addEvent(event: EventEntity) = api.addEvent(event)
}*/