package com.example.plugd.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
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

    fun loginWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                val user = repository.loginWithCredential(credential) // Add in AuthRepository
                _authState.value = Result.success(user)
            } catch (e: Exception) {
                _authState.value = Result.failure(e)
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout() // ✅ This works if repository is AuthRepository
            onComplete()
        }
    }
}














/* working
package com.example.plugd.ui.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<Result<UserEntity>?>(null)
    val authState: StateFlow<Result<UserEntity>?> get() = _authState

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
}
*/












/*package com.example.plugd.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth: FirebaseAuth,
    private val repository: AuthRepository
) : ViewModel() {


    fun login(email: String, password: String, onResult: (Result<UserEntity>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = repository.login(email, password)
                onResult(Result.success(user))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }

    fun register(name: String, username: String, email: String, password: String, onResult: (Result<UserEntity>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = repository.register(name, username, email, password)
                onResult(Result.success(user))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}*/









/*package com.example.plugd.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plugd.data.localRoom.dao.UserDao
import com.example.plugd.data.localRoom.entity.UserEntity
import com.example.plugd.remote.firebase.FirebaseAuthService
import kotlinx.coroutines.launch

class AuthViewModel(private val authService: FirebaseAuthService, private val userDao: UserDao) : ViewModel() {
    fun loginWithEmail(email: String, password: String, onResult: (Result<UserEntity>) -> Unit) {
        viewModelScope.launch {
            try {
                val firebaseUser = authService.loginUser(email, password)
                if (firebaseUser != null) {
                    // in practice fetch remote user and insert local (omitted for brevity)
                    val local = userDao.getUserByEmailAndPassword(email, password)
                    if (local != null) onResult(Result.success(local)) else onResult(Result.failure(Exception("No local user")))
                } else {
                    val local = userDao.getUserByEmailAndPassword(email, password)
                    if (local != null) onResult(Result.success(local)) else onResult(Result.failure(Exception("Login failed")))
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}

    /*var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            try {
                authRepository.changePassword(current, new)
                uiState = AuthUiState.Success
            } catch (e: Exception) {
                uiState = AuthUiState.Error(e.message ?: "Failed to update password")
            }
        }
    }


    fun resetPassword(email: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            try {
                authRepository.sendPasswordResetEmail(email)
                uiState = AuthUiState.Success
            } catch (e: Exception) {
                uiState = AuthUiState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }

    fun resetUiState() {
        uiState = AuthUiState.Idle
    }*/














/*class AuthViewModel(private val authService: FirebaseAuthService, private val userDao: UserDao) : ViewModel() {
    fun loginWithEmail(email: String, password: String, onResult: (Result<UserEntity>) -> Unit) {
        viewModelScope.launch {
            try {
                val firebaseUser = authService.loginUser(email, password)
                if (firebaseUser != null) {
                    // in practice fetch remote user and insert local (omitted for brevity)
                    val local = userDao.getUserByEmailAndPassword(email, password)
                    if (local != null) onResult(Result.success(local)) else onResult(Result.failure(Exception("No local user")))
                } else {
                    val local = userDao.getUserByEmailAndPassword(email, password)
                    if (local != null) onResult(Result.success(local)) else onResult(Result.failure(Exception("Login failed")))
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
            }
        }
    }
}
*/