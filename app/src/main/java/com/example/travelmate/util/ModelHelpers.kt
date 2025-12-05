package com.example.travelmate.util

import com.example.travelmate.data.models.*
import com.google.gson.Gson

object ModelHelpers {
    private val gson = Gson()
    
    fun createTravel(
        id: String,
        title: String,
        description: String? = null,
        destination: String,
        startDate: Long,
        endDate: Long,
        organiserId: String,
        participantIds: List<String> = emptyList(),
        budget: Double = 0.0,
        spentAmount: Double = 0.0,
        imageUrl: String? = null,
        itinerary: List<ItineraryItem> = emptyList(),
        createdAt: Long = System.currentTimeMillis()
    ): Travel {
        return Travel(
            id = id,
            title = title,
            description = description,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            organiserId = organiserId,
            participantIds = participantIds,
            budget = budget,
            spentAmount = spentAmount,
            imageUrl = imageUrl,
            itinerary = itinerary,
            createdAt = createdAt
        )
    }
    
    fun createActivity(
        id: String,
        travelId: String,
        title: String,
        description: String? = null,
        date: Long,
        time: String? = null,
        location: String? = null,
        assignedParticipantIds: List<String> = emptyList(),
        cost: Double = 0.0,
        category: ActivityCategory = ActivityCategory.OTHER,
        imageUrl: String? = null,
        createdAt: Long = System.currentTimeMillis()
    ): Activity {
        return Activity(
            id = id,
            travelId = travelId,
            title = title,
            description = description,
            date = date,
            time = time,
            location = location,
            assignedParticipantIds = assignedParticipantIds,
            cost = cost,
            category = category,
            imageUrl = imageUrl,
            createdAt = createdAt
        )
    }
    
    fun createBudgetItem(
        id: String,
        travelId: String,
        title: String,
        amount: Double,
        category: BudgetCategory,
        paidByUserId: String,
        sharedWithUserIds: List<String> = emptyList(),
        date: Long = System.currentTimeMillis(),
        description: String? = null,
        imageUrl: String? = null
    ): BudgetItem {
        return BudgetItem(
            id = id,
            travelId = travelId,
            title = title,
            amount = amount,
            category = category,
            paidByUserId = paidByUserId,
            sharedWithUserIds = sharedWithUserIds,
            date = date,
            description = description,
            imageUrl = imageUrl
        )
    }
}

