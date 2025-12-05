package com.example.travelmate

import android.app.Application
import com.example.travelmate.data.firebase.FirebaseAuthService
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.data.firebase.FirebaseSyncService
import com.example.travelmate.data.room.TravelMateDatabase
import com.example.travelmate.util.NetworkMonitor
import com.example.travelmate.util.SessionManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class TravelMateApplication : Application() {
    val database by lazy { TravelMateDatabase.getDatabase(this) }
    val firebaseAuthService by lazy { FirebaseAuthService() }
    val firebaseRealtimeService by lazy { FirebaseRealtimeService() }
    val networkMonitor by lazy { NetworkMonitor(this) }
    val firebaseSyncService by lazy { 
        FirebaseSyncService(
            database.travelDao(),
            database.activityDao(),
            database.messageDao(),
            database.budgetDao(),
            database.notificationDao()
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            
            // Enable Firebase Realtime Database persistence for offline support
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            
            // Start syncing if user is logged in
            val userId = SessionManager.getCurrentUserId()
            if (userId != null) {
                firebaseSyncService.startSyncing(userId)
            }
        } catch (e: Exception) {
            // Firebase not configured yet - app will work in local-only mode
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        networkMonitor.cleanup()
    }
    
    companion object {
        lateinit var instance: TravelMateApplication
            private set
    }
}

