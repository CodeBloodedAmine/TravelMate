package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Travel
import com.example.travelmate.data.room.TravelDao
import com.example.travelmate.data.firebase.FirebaseRealtimeService
import com.example.travelmate.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TravelRepositoryHybrid(
    private val travelDao: TravelDao,
    private val firebaseService: FirebaseRealtimeService,
    private val networkMonitor: NetworkMonitor
) {
    fun getTravelById(travelId: String): Flow<Travel?> {
        return if (networkMonitor.isNetworkAvailable()) {
            firebaseService.observeTravel(travelId)
        } else {
            travelDao.getTravelById(travelId)
        }
    }

    fun getOrganisedTravels(userId: String): Flow<List<Travel>> {
        return if (networkMonitor.isNetworkAvailable()) {
            firebaseService.observeTravels(userId)
        } else {
            travelDao.getOrganisedTravels(userId)
        }
    }

    fun getAllTravels(): Flow<List<Travel>> {
        return if (networkMonitor.isNetworkAvailable()) {
            // Prefer Firebase; if Firebase is empty (rules/data), fallback to local cache
            combine(
                firebaseService.observeAllTravels(),
                travelDao.getAllTravels()
            ) { remote, local ->
                if (remote.isNotEmpty()) remote else local
            }
        } else {
            // Fallback to local cache when offline
            travelDao.getAllTravels()
        }
    }

    suspend fun insertTravel(travel: Travel) {
        try {
            travelDao.insertTravel(travel)

            if (networkMonitor.isNetworkAvailable()) {
                firebaseService.saveTravel(travel)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    suspend fun updateTravel(travel: Travel) {
        travelDao.updateTravel(travel)

        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.saveTravel(travel)
        }
    }

    suspend fun deleteTravel(travel: Travel) {
        travelDao.deleteTravel(travel)

        if (networkMonitor.isNetworkAvailable()) {
            firebaseService.deleteTravel(travel.id)
        }
    }

    suspend fun participateInTravel(travelId: String, userId: String) {
        try {
            val travelResult = firebaseService.getTravelById(travelId)
            val travel = travelResult ?: return

            if (!travel.participantIds.contains(userId)) {
                val updatedTravel = travel.copy(
                    participantIds = travel.participantIds + userId
                )

                travelDao.updateTravel(updatedTravel)

                if (networkMonitor.isNetworkAvailable()) {
                    firebaseService.saveTravel(updatedTravel)
                }
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    suspend fun notifyAllUsersOfNewTravel(travel: Travel) {
        try {
            if (networkMonitor.isNetworkAvailable()) {
                firebaseService.createNotificationForAllUsers(travel)
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
}

