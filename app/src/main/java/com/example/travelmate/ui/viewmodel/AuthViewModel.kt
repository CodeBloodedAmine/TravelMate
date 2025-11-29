package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.models.User
import com.example.travelmate.data.models.UserRole
import com.example.travelmate.data.repository.UserRepository
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState(isLoggedIn = SessionManager.isLoggedIn()))
    val authState: StateFlow<AuthState> = _authState
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        _authState.value = _authState.value.copy(isLoggedIn = SessionManager.isLoggedIn())
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val user = userRepository.getUserByEmail(email)
                
                if (user != null) {
                    // In a real app, verify password hash here
                    SessionManager.saveSession(user.id, user.email, user.name)
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        errorMessage = null
                    )
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        errorMessage = "Email ou mot de passe incorrect"
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Erreur de connexion: ${e.message}"
                )
            }
        }
    }
    
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Check if user already exists
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "Un compte avec cet email existe déjà"
                    )
                    return@launch
                }
                
                // Create new user
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    email = email,
                    name = name,
                    role = UserRole.PARTICIPANT,
                    createdAt = System.currentTimeMillis()
                )
                
                userRepository.insertUser(newUser)
                SessionManager.saveSession(newUser.id, newUser.email, newUser.name)
                
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Erreur d'inscription: ${e.message}"
                )
            }
        }
    }
    
    fun logout() {
        SessionManager.clearSession()
        _authState.value = AuthState(isLoggedIn = false)
    }
}

