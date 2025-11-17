package com.example.plugd.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.EventEntity
import com.example.plugd.data.repository.EventRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    val events: StateFlow<List<EventEntity>> =
        repository.events.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // A dedicated flow that filters events for only the current user.
    val userEvents: StateFlow<List<EventEntity>> = repository.events
        .map { allEvents ->
            val uid = auth.currentUser?.uid
            if (uid != null) {
                // Filters events where the user is the creator
                allEvents.filter { it.userId == uid || it.ownerUid == uid }
            } else {
                emptyList() // If no user is logged in, return an empty list
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createEvent(
        name: String,
        category: String,
        description: String,
        location: String,
        latitude: Double? = null,
        longitude: Double? = null,
        date: Long,
        supportDocs: String? = null,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                repository.createEvent(
                    name = name,
                    category = category,
                    description = description,
                    location = location,
                    latitude = latitude,
                    longitude = longitude,
                    date = date,
                    supportDocs = supportDocs
                )
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error creating event: ${e.localizedMessage}")
                onError?.invoke(e)
            }
        }
    }

    fun addEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                repository.addEvent(event)
            } catch (e: retrofit2.HttpException) {
                Log.e("EventViewModel", "HTTP ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Unexpected: ${e.localizedMessage}")
            }
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                repository.refreshEvents()
            } catch (e: retrofit2.HttpException) {
                Log.e("EventViewModel", "HTTP ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Unexpected: ${e.localizedMessage}")
            }
        }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                repository.updateEvent(event)
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error updating event: ${e.message}", e)
            }
        }
    }


    // The missing function to delete an event, called by the EditProfileScreen.
    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            try {
                repository.deleteEvent(eventId)
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error deleting event: ${e.message}", e)
            }
        }
    }
}


















/*package com.example.plugd.viewmodels

import EventRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.EventEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.util.Log

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    val events: StateFlow<List<EventEntity>> =
        repository.events.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                repository.addEvent(event)
            } catch (e: retrofit2.HttpException) {
                Log.e("EventViewModel", "HTTP error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                repository.loadEventsFromApi()
            } catch (e: retrofit2.HttpException) {
                Log.e("EventViewModel", "HTTP error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    fun syncEvents() {
        viewModelScope.launch {
            repository.syncEvents()
        }
    }
}*/















/*package com.example.plugd.viewmodels

import EventRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.EventEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    val events: StateFlow<List<EventEntity>> =
        repository.events.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addEvent(event: EventEntity) {
        viewModelScope.launch {
            try {
                repository.addEvent(event)
            } catch (e: retrofit2.HttpException) {
                // Network issue from API
                Log.e("EventViewModel", "HTTP error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                // Other errors
                Log.e("EventViewModel", "Unexpected error: ${e.localizedMessage}")
            }
        }
    }


    fun loadEvents() {
        viewModelScope.launch {
            try {
                repository.loadEvents()
            } catch (e: retrofit2.HttpException) {
                Log.e("EventViewModel", "HTTP error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Unexpected error: ${e.localizedMessage}")
            }
        }
    }
}*/