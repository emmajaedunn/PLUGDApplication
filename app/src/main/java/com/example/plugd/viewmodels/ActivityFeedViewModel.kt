package com.example.plugd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.example.plugd.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ActivityFeedViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityEntity>>(emptyList())
    val activities: StateFlow<List<ActivityEntity>> = _activities.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getActivities()
                .catch { e ->
                    _error.value = "Failed to load activity feed: ${e.message}"
                }
                .collect { activitiesFromRepo ->
                    _activities.value = activitiesFromRepo
                }
        }
    }

    fun refreshActivities() {
        // The real-time listener in getActivities() means a manual refresh
        // is often not needed, but this function can be used to force a re-fetch if desired.
        loadActivities()
    }

    fun clearError() {
        _error.value = null
    }
}
