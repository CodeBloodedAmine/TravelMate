package com.example.travelmate.data.room

import androidx.room.*
import com.example.travelmate.data.models.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities WHERE id = :activityId")
    fun getActivityById(activityId: String): Flow<Activity?>

    @Query("SELECT * FROM activities WHERE travelId = :travelId ORDER BY date ASC")
    fun getActivitiesByTravel(travelId: String): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE travelId = :travelId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getActivitiesByDateRange(travelId: String, startDate: Long, endDate: Long): Flow<List<Activity>>

    // Search in JSON string - Room doesn't support native JSON queries
    @Query("SELECT * FROM activities WHERE assignedParticipantIdsJson LIKE '%\"' || :userId || '\"%'")
    fun getActivitiesByUser(userId: String): Flow<List<Activity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: Activity)

    @Update
    suspend fun updateActivity(activity: Activity)

    @Delete
    suspend fun deleteActivity(activity: Activity)

    @Query("DELETE FROM activities WHERE travelId = :travelId")
    suspend fun deleteActivitiesByTravel(travelId: String)

    @Query("DELETE FROM activities")
    suspend fun deleteAllActivities()
}

