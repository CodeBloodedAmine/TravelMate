package com.example.travelmate.data.room

import androidx.room.*
import com.example.travelmate.data.models.Travel
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {
    @Query("SELECT * FROM travels WHERE id = :travelId")
    fun getTravelById(travelId: String): Flow<Travel?>

    @Query("SELECT * FROM travels WHERE organiserId = :userId")
    fun getTravelsByUser(userId: String): Flow<List<Travel>>

    @Query("SELECT * FROM travels WHERE organiserId = :userId")
    fun getOrganisedTravels(userId: String): Flow<List<Travel>>

    @Query("SELECT * FROM travels")
    fun getAllTravels(): Flow<List<Travel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravel(travel: Travel)

    @Update
    suspend fun updateTravel(travel: Travel)

    @Delete
    suspend fun deleteTravel(travel: Travel)

    @Query("DELETE FROM travels")
    suspend fun deleteAllTravels()
}

