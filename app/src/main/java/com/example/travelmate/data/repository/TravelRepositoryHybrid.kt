package com.example.travelmate.data.repository

import android.util.Log
import com.example.travelmate.data.models.Travel
import com.example.travelmate.data.room.TravelDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge

class TravelRepositoryHybrid(
    private val travelDao: TravelDao,
    private val firebaseService: FirebaseRealtimeService,
    private val networkMonitor: NetworkMonitor
) {
    fun getTravelById(travelId: String): Flow<Travel?> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase for real-time updates
            firebaseService.observeTravel(travelId)
        } else {
            // Use local Room database when offline
            travelDao.getTravelById(travelId)
        }
    }

    fun getOrganisedTravels(userId: String): Flow<List<Travel>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase for real-time updates
            firebaseService.observeTravels(userId)
        } else {
            // Use local Room database when offline
            travelDao.getOrganisedTravels(userId)
        }
    }

    fun getAllTravels(): Flow<List<Travel>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Use Firebase to get ALL travels
            firebaseService.observeAllTravels()
        } else {
            // Use local Room database when offline
            travelDao.getAllTravels()
        }
    }

    suspend fun insertTravel(travel: Travel) {
        // Save to local first for immediate UI update
        travelDao.insertTravel(travel)

        // Then sync to Firebase if online
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveTravel(travel)
        }
    }

    suspend fun updateTravel(travel: Travel) {
        // Update local first
        travelDao.updateTravel(travel)

        // Then sync to Firebase if online
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveTravel(travel)
        }
    }

    suspend fun deleteTravel(travel: Travel) {
        // Delete from local first
        travelDao.deleteTravel(travel)

        // Then sync to Firebase if online
        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.deleteTravel(travel.id)
        }
    }

    suspend fun participateInTravel(travelId: String, userId: String) {
        try {
            Log.d("TravelRepository", "User $userId joining travel $travelId")

            // Get the travel from Firebase (primary source)
            val travelResult = firebaseService.getTravelById(travelId)
            val travel = travelResult ?: run {
                Log.e("TravelRepository", "Travel not found: $travelId")
                return
            }

            // Add user to participants if not already there
            if (!travel.participantIds.contains(userId)) {
                val updatedTravel = travel.copy(
                    participantIds = travel.participantIds + userId
                )

                // Update locally
                travelDao.updateTravel(updatedTravel)

                // Sync to Firebase
                if (networkMonitor.isNetworkAvailable()) {
                    firebaseService.saveTravel(updatedTravel)
                    Log.d("TravelRepository", "✅ User added to travel participants")
                }
            } else {
                Log.d("TravelRepository", "User already a participant")
            }
        } catch (e: Exception) {
            Log.e("TravelRepository", "Error joining travel: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun notifyAllUsersOfNewTravel(travel: Travel) {
        try {
            Log.d("TravelRepository", "Creating notifications for new travel: ${travel.title}")
            if (networkMonitor.isNetworkAvailable()) {
                firebaseService.createNotificationForAllUsers(travel)
            }
        } catch (e: Exception) {
            Log.e("TravelRepository", "Error creating notifications: ${e.message}")
        }
    }
}

