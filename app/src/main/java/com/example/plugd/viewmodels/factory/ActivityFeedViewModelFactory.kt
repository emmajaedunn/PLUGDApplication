package com.example.plugd.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.plugd.data.repository.ActivityRepository
import com.example.plugd.viewmodels.ActivityFeedViewModel

class ActivityFeedViewModelFactory(
    private val repository: ActivityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityFeedViewModel::class.java)) {
            return ActivityFeedViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}