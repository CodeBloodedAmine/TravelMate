package com.example.travelmate.util

import android.content.Context
import android.content.SharedPreferences
import com.example.travelmate.TravelMateApplication
import com.example.travelmate.data.models.UserRole

object SessionManager {
    private const val PREFS_NAME = "TravelMateSession"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    private val prefs: SharedPreferences
        get() = TravelMateApplication.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun saveSession(userId: String, email: String, name: String, role: UserRole = UserRole.PARTICIPANT) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role.name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getCurrentUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getCurrentUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getCurrentUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun getUserRole(): UserRole? {
        val roleName = prefs.getString(KEY_USER_ROLE, null)
        return if (roleName != null) UserRole.valueOf(roleName) else null
    }
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
            remove(KEY_USER_ROLE)
            remove(KEY_IS_LOGGED_IN)
            apply()
        }
    }
}

