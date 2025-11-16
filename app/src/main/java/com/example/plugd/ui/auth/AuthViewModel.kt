package com.example.plugd.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.repository.AuthRepository
import com.example.plugd.data.repository.ProfileRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<Result<UserEntity>?>(null)
    val authState: StateFlow<Result<UserEntity>?> get() = _authState

    fun register(name: String, username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.register(name, username, email, password)
                _authState.value = Result.success(user)
            } catch (e: Exception) {
                _authState.value = Result.failure(e)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = repository.login(email, password)
                _authState.value = Result.success(user)
            } catch (e: Exception) {
                _authState.value = Result.failure(e)
            }
        }
    }

    fun loginWithGoogle(credential: AuthCredential) = viewModelScope.launch {
        try {
            val user = repository.loginWithCredential(credential)
            _authState.value = Result.success(user)
        } catch (e: Exception) {
            _authState.value = Result.failure(e)
        }
    }

    fun clearAuthState() {
        _authState.value = null
    }

    fun resetPassword(email: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank()) return
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}
