package com.example.travelmate.data.repository

import com.example.travelmate.data.models.Activity
import com.example.travelmate.data.room.ActivityDao
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    fun getActivityById(activityId: String): Flow<Activity?> = activityDao.getActivityById(activityId)

    fun getActivitiesByTravel(travelId: String): Flow<List<Activity>> = activityDao.getActivitiesByTravel(travelId)

    fun getActivitiesByDateRange(travelId: String, startDate: Long, endDate: Long): Flow<List<Activity>> =
        activityDao.getActivitiesByDateRange(travelId, startDate, endDate)

    fun getActivitiesByUser(userId: String): Flow<List<Activity>> = activityDao.getActivitiesByUser(userId)

    suspend fun insertActivity(activity: Activity) = activityDao.insertActivity(activity)

    suspend fun updateActivity(activity: Activity) = activityDao.updateActivity(activity)

    suspend fun deleteActivity(activity: Activity) = activityDao.deleteActivity(activity)
}
