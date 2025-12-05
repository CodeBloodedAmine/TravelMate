package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelmate.data.firebase.FirebaseAuthService
import com.example.travelmate.data.models.UserRole
import com.example.travelmate.data.repository.UserRepository
import com.example.travelmate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val firebaseAuthService: FirebaseAuthService,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState(isLoggedIn = SessionManager.isLoggedIn()))
    val authState: StateFlow<AuthState> = _authState
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        val firebaseUser = firebaseAuthService.getCurrentUser()
        val isLoggedIn = firebaseUser != null && SessionManager.isLoggedIn()
        _authState.value = _authState.value.copy(isLoggedIn = isLoggedIn)
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = firebaseAuthService.signIn(email, password)
                
                result.onSuccess { firebaseUser ->
                    // Get user profile from Firebase
                    val profileResult = firebaseAuthService.getUserProfile(firebaseUser.uid)
                    profileResult.onSuccess { user ->
                        if (user != null) {
                            // Save to local Room for offline access
                            userRepository.insertUser(user)
                            SessionManager.saveSession(user.id, user.email, user.name, user.role)
                            
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                errorMessage = null
                            )
                        } else {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = false,
                                errorMessage = "Profil utilisateur introuvable"
                            )
                        }
                    }.onFailure { e ->
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            errorMessage = "Erreur lors de la récupération du profil: ${e.message}"
                        )
                    }
                }.onFailure { e ->
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

    fun register(name: String, email: String, password: String, role: UserRole = UserRole.PARTICIPANT) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)

            try {
                val result = firebaseAuthService.signUp(email, password, name, role)

                result.onSuccess { firebaseUser ->
                    // User profile is already created in Firebase by FirebaseAuthService
                    val profileResult = firebaseAuthService.getUserProfile(firebaseUser.uid)
                    profileResult.onSuccess { user ->
                        if (user != null) {
                            // Save to local Room for offline access
                            userRepository.insertUser(user)
                            SessionManager.saveSession(user.id, user.email, user.name, user.role)

                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                errorMessage = null
                            )
                        } else {
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isLoggedIn = false,
                                errorMessage = "Erreur lors de la création du profil"
                            )
                        }
                    }.onFailure { e ->  // error handler
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            errorMessage = "Erreur lors de la récupération du profil: ${e.message}"
                        )
                    }
                }.onFailure { e ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = when {
                            e.message?.contains("email-already-in-use") == true ->
                                "Un compte avec cet email existe déjà"
                            e.message?.contains("weak-password") == true ->
                                "Le mot de passe est trop faible"
                            else -> "Erreur d'inscription: ${e.message}"
                        }
                    )
                }
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = "Erreur d'inscription: ${e.message}"
                )
            }
        }
    }
    
    fun logout() {
        firebaseAuthService.signOut()
        SessionManager.clearSession()
        _authState.value = AuthState(isLoggedIn = false)
    }
}
