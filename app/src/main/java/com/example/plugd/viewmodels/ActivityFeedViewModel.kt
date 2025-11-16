package com.example.plugd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.ActivityEntity
import com.example.plugd.data.repository.ActivityRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ActivityFeedViewModel(
    private val repository: ActivityRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityEntity>>(emptyList())
    val activities: StateFlow<List<ActivityEntity>> = _activities.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadActivities()
    }

    private fun loadActivities() {
        loadJob?.cancel() // in case you call refresh
        loadJob = viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                _activities.value = emptyList()
                _error.value = "Not logged in"
                return@launch
            }
            val userId = currentUser.uid

            repository.getActivitiesForUser(userId)
                .catch { e ->
                    _error.value = "Failed to load activity feed: ${e.message}"
                }
                .collect { activitiesFromRepo ->
                    _activities.value = activitiesFromRepo
                }
        }
    }

    fun refreshActivities() {
        loadActivities()
    }

    fun clearError() {
        _error.value = null
    }
}