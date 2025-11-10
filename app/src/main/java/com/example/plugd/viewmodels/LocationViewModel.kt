package com.example.plugd.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(app: Application) : AndroidViewModel(app) {
    private val locationRepo = LocationRepository(app.applicationContext)

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location = _location.asStateFlow()

    fun fetchLocation() {
        viewModelScope.launch {
            _location.value = locationRepo.getCurrentLocation()
        }
    }
}