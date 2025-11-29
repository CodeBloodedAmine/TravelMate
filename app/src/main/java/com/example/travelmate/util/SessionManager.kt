package com.example.travelmate.util

import android.content.Context
import android.content.SharedPreferences
import com.example.travelmate.TravelMateApplication

object SessionManager {
    private const val PREFS_NAME = "TravelMateSession"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    private val prefs: SharedPreferences
        get() = TravelMateApplication.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun saveSession(userId: String, email: String, name: String) {
        prefs.edit().apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getCurrentUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getCurrentUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getCurrentUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

