package com.example.plugd.data.repository

import com.example.plugd.data.localRoom.dao.EventDao
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.model.UserProfile
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
    private val usersCollection = firestore.collection("users")   // ✅ for reading name/username
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()  // ✅ current user

    val events: Flow<List<EventEntity>> = eventDao.getAllEvents()

    /**
     * 🔥 New: create event using REAL name + username from Firestore.
     */
    suspend fun createEvent(
        name: String,
        category: String,
        description: String,
        location: String,
        latitude: Double? = null,
        longitude: Double? = null,
        date: Long,
        supportDocs: String? = null
    ) {
        val user = auth.currentUser ?: throw Exception("Not logged in")
        val uid = user.uid

        // ✅ Get the stored name + username from Firestore
        val userSnap = usersCollection.document(uid).get().await()
        val fullName = userSnap.getString("name") ?: ""
        val username = userSnap.getString("username") ?: ""

        // Generate Firestore ID
        val docRef = eventsCollection.document()
        val eventId = docRef.id

        val event = EventEntity(
            eventId = eventId,
            userId = uid,
            name = name,
            category = category,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude,
            date = date,
            createdBy = uid,
            createdByName = fullName,
            createdByUsername = username,
            supportDocs = supportDocs,
            ownerUid = uid
        )

        // Save to Firestore
        docRef.set(event).await()

        // Cache in Room
        eventDao.insertEvent(event)
    }

    // You can keep this for any old code still calling addEvent(event)
    suspend fun addEvent(event: EventEntity) {
        eventsCollection.document(event.eventId).set(event).await()
        eventDao.insertEvent(event)
    }

    suspend fun refreshEvents() {
        val snapshot = eventsCollection.get().await()
        val firestoreEvents = snapshot.toObjects(EventEntity::class.java)
        eventDao.clearEvents()
        eventDao.insertAll(firestoreEvents)
    }

    suspend fun updateEvent(event: EventEntity) {
        eventDao.updateEvent(event)
        firestore.collection("events").document(event.eventId).set(event).await()
    }

    suspend fun deleteEvent(eventId: String) {
        eventsCollection.document(eventId).delete().await()
        eventDao.deleteEventById(eventId)
    }
}

















/*package com.example.plugd.data.repository

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

    /**
     * 🔥 New: create event using REAL name + username from Firestore.
     */
    suspend fun createEvent(
        name: String,
        category: String,
        description: String,
        location: String,
        latitude: Double? = null,
        longitude: Double? = null,
        date: Long,
        supportDocs: String? = null
    ) {
        val user = auth.currentUser ?: throw Exception("Not logged in")
        val uid = user.uid

        // Fetch full profile from Firestore
        val userSnap = usersCollection.document(uid).get().await()
        val fullName = userSnap.getString("name") ?: ""
        val username = userSnap.getString("username") ?: ""

        // Generate Firestore ID
        val docRef = eventsCollection.document()
        val eventId = docRef.id

        val event = EventEntity(
            eventId = eventId,
            userId = uid,
            name = name,
            category = category,
            description = description,
            location = location,
            latitude = latitude,
            longitude = longitude,
            date = date,
            createdBy = uid,
            createdByName = fullName,
            createdByUsername = username,
            supportDocs = supportDocs,
            ownerUid = uid
        )

        // Save to Firestore
        docRef.set(event).await()

        // Cache in Room
        eventDao.insertEvent(event)
    }


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

    suspend fun deleteEvent(eventId: String) {
        // 1. Delete from the remote Firestore database
        eventsCollection.document(eventId).delete().await()

        // 2. Delete from the local Room database using the new DAO function
        eventDao.deleteEventById(eventId)
    }
}*/








