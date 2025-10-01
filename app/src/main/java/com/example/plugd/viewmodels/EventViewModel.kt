package com.example.plugd.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.repository.EventRepository
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
}