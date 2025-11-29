package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Travel
import com.example.travelmate.data.room.TravelDao
import kotlinx.coroutines.flow.Flow

class TravelRepository(private val travelDao: TravelDao) {
    fun getTravelById(travelId: String): Flow<Travel?> = travelDao.getTravelById(travelId)
    
    fun getAllTravels(): Flow<List<Travel>> = travelDao.getAllTravels()
    
    fun getOrganisedTravels(userId: String): Flow<List<Travel>> = travelDao.getOrganisedTravels(userId)
    
    suspend fun insertTravel(travel: Travel) = travelDao.insertTravel(travel)
    
    suspend fun updateTravel(travel: Travel) = travelDao.updateTravel(travel)
    
    suspend fun deleteTravel(travel: Travel) = travelDao.deleteTravel(travel)
    
    // Helper function to get travels where user is participant or organiser
    // Note: This is a simplified version. In production, you'd need to:
    // 1. Create a separate TravelParticipant table for many-to-many relationship
    // 2. Or filter travels in memory by checking participantIds JSON
    // For now, this returns organized travels only
    suspend fun getTravelsForUser(userId: String): List<Travel> {
        // This method is kept for backward compatibility
        // Use getOrganisedTravels(userId) Flow directly in ViewModels instead
        return emptyList()
    }
}

