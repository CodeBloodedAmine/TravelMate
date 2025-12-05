package com.example.travelmate.data.firebase

import com.example.travelmate.data.models.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRealtimeService {
    private val database = FirebaseDatabase.getInstance().reference
    
    // Travels
    fun observeTravels(userId: String): Flow<List<Travel>> = callbackFlow {
        val listener = database.child("travels")
            .orderByChild("organiserId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val travels = snapshot.children.mapNotNull { travelSnapshot ->
                        try {
                            val travelMap = travelSnapshot.value as? Map<*, *> ?: return@mapNotNull null
                            
                            val participantIdsList = when (val participants = travelMap["participantIds"]) {
                                is Map<*, *> -> participants.keys.mapNotNull { it.toString() }
                                is List<*> -> participants.mapNotNull { it.toString() }
                                else -> emptyList<String>()
                            }
                            
                            val itineraryList = when (val itinerary = travelMap["itinerary"]) {
                                is Map<*, *> -> itinerary.values.mapNotNull { item ->
                                    val itemMap = item as? Map<*, *> ?: return@mapNotNull null
                                    ItineraryItem(
                                        id = itemMap["id"]?.toString() ?: "",
                                        date = (itemMap["date"] as? Number)?.toLong() ?: 0L,
                                        time = itemMap["time"]?.toString(),
                                        title = itemMap["title"]?.toString() ?: "",
                                        description = itemMap["description"]?.toString(),
                                        location = itemMap["location"]?.toString()
                                    )
                                }
                                is List<*> -> itinerary.mapNotNull { item ->
                                    val itemMap = item as? Map<*, *> ?: return@mapNotNull null
                                    ItineraryItem(
                                        id = itemMap["id"]?.toString() ?: "",
                                        date = (itemMap["date"] as? Number)?.toLong() ?: 0L,
                                        time = itemMap["time"]?.toString(),
                                        title = itemMap["title"]?.toString() ?: "",
                                        description = itemMap["description"]?.toString(),
                                        location = itemMap["location"]?.toString()
                                    )
                                }
                                else -> emptyList<ItineraryItem>()
                            }
                            
                            Travel(
                                id = travelMap["id"]?.toString() ?: travelSnapshot.key ?: "",
                                title = travelMap["title"]?.toString() ?: "",
                                description = travelMap["description"]?.toString(),
                                destination = travelMap["destination"]?.toString() ?: "",
                                startDate = (travelMap["startDate"] as? Number)?.toLong() ?: 0L,
                                endDate = (travelMap["endDate"] as? Number)?.toLong() ?: 0L,
                                organiserId = travelMap["organiserId"]?.toString() ?: "",
                                participantIds = participantIdsList,
                                budget = (travelMap["budget"] as? Number)?.toDouble() ?: 0.0,
                                spentAmount = (travelMap["spentAmount"] as? Number)?.toDouble() ?: 0.0,
                                imageUrl = travelMap["imageUrl"]?.toString(),
                                itinerary = itineraryList,
                                createdAt = (travelMap["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(travels)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.removeEventListener(listener) }
    }
    
    fun observeAllTravels(): Flow<List<Travel>> = callbackFlow {
        val listener = database.child("travels")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val travels = snapshot.children.mapNotNull { travelSnapshot ->
                        // First try direct mapping
                        val direct = travelSnapshot.getValue(Travel::class.java)
                        if (direct != null) return@mapNotNull direct
                        
                        // Fallback manual parsing if shape differs
                        try {
                            val travelMap = travelSnapshot.value as? Map<*, *> ?: return@mapNotNull null
                            
                            val participantIdsList = when (val participants = travelMap["participantIds"]) {
                                is Map<*, *> -> participants.keys.mapNotNull { it.toString() }
                                is List<*> -> participants.mapNotNull { it.toString() }
                                else -> emptyList<String>()
                            }
                            
                            val itineraryList = when (val itinerary = travelMap["itinerary"]) {
                                is Map<*, *> -> itinerary.values.mapNotNull { item ->
                                    val itemMap = item as? Map<*, *> ?: return@mapNotNull null
                                    ItineraryItem(
                                        id = itemMap["id"]?.toString() ?: "",
                                        date = (itemMap["date"] as? Number)?.toLong() ?: 0L,
                                        time = itemMap["time"]?.toString(),
                                        title = itemMap["title"]?.toString() ?: "",
                                        description = itemMap["description"]?.toString(),
                                        location = itemMap["location"]?.toString()
                                    )
                                }
                                is List<*> -> itinerary.mapNotNull { item ->
                                    val itemMap = item as? Map<*, *> ?: return@mapNotNull null
                                    ItineraryItem(
                                        id = itemMap["id"]?.toString() ?: "",
                                        date = (itemMap["date"] as? Number)?.toLong() ?: 0L,
                                        time = itemMap["time"]?.toString(),
                                        title = itemMap["title"]?.toString() ?: "",
                                        description = itemMap["description"]?.toString(),
                                        location = itemMap["location"]?.toString()
                                    )
                                }
                                else -> emptyList<ItineraryItem>()
                            }
                            
                            Travel(
                                id = travelMap["id"]?.toString() ?: travelSnapshot.key ?: "",
                                title = travelMap["title"]?.toString() ?: "",
                                description = travelMap["description"]?.toString(),
                                destination = travelMap["destination"]?.toString() ?: "",
                                startDate = (travelMap["startDate"] as? Number)?.toLong() ?: 0L,
                                endDate = (travelMap["endDate"] as? Number)?.toLong() ?: 0L,
                                organiserId = travelMap["organiserId"]?.toString() ?: "",
                                participantIds = participantIdsList,
                                budget = (travelMap["budget"] as? Number)?.toDouble() ?: 0.0,
                                spentAmount = (travelMap["spentAmount"] as? Number)?.toDouble() ?: 0.0,
                                imageUrl = travelMap["imageUrl"]?.toString(),
                                itinerary = itineraryList,
                                createdAt = (travelMap["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(travels)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { 
            database.child("travels").removeEventListener(listener) 
        }
    }
    
    fun observeTravel(travelId: String): Flow<Travel?> = callbackFlow {
        val listener = database.child("travels").child(travelId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val travel = parseTravelFromSnapshot(snapshot)
                    trySend(travel)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.child("travels").child(travelId).removeEventListener(listener) }
    }
    
    suspend fun getTravelById(travelId: String): Travel? {
        return try {
            val snapshot = database.child("travels").child(travelId).get().await()
            parseTravelFromSnapshot(snapshot)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseTravelFromSnapshot(snapshot: DataSnapshot): Travel? {
        return try {
            val travelMap = snapshot.value as? Map<*, *> ?: return null
            
            val participantIdsList = when (val participants = travelMap["participantIds"]) {
                is Map<*, *> -> participants.keys.mapNotNull { it.toString() }
                is List<*> -> participants.mapNotNull { it.toString() }
                else -> emptyList<String>()
            }
            
            val itineraryList = when (val itinerary = travelMap["itinerary"]) {
                is Map<*, *> -> itinerary.values.mapNotNull { item ->
                    val itemMap = item as? Map<*, *> ?: return@mapNotNull null
                    ItineraryItem(
                        id = itemMap["id"]?.toString() ?: "",
                        date = (itemMap["date"] as? Number)?.toLong() ?: 0L,
                        time = itemMap["time"]?.toString(),
                        title = itemMap["title"]?.toString() ?: "",
                        description = itemMap["description"]?.toString(),
                        location = itemMap["location"]?.toString()
                    )
                }
                is List<*> -> itinerary.mapNotNull { item ->
                    val itemMap = item as? Map<*, *> ?: return@mapNotNull null
                    ItineraryItem(
                        id = itemMap["id"]?.toString() ?: "",
                        date = (itemMap["date"] as? Number)?.toLong() ?: 0L,
                        time = itemMap["time"]?.toString(),
                        title = itemMap["title"]?.toString() ?: "",
                        description = itemMap["description"]?.toString(),
                        location = itemMap["location"]?.toString()
                    )
                }
                else -> emptyList<ItineraryItem>()
            }
            
            Travel(
                id = travelMap["id"]?.toString() ?: snapshot.key ?: "",
                title = travelMap["title"]?.toString() ?: "",
                description = travelMap["description"]?.toString(),
                destination = travelMap["destination"]?.toString() ?: "",
                startDate = (travelMap["startDate"] as? Number)?.toLong() ?: 0L,
                endDate = (travelMap["endDate"] as? Number)?.toLong() ?: 0L,
                organiserId = travelMap["organiserId"]?.toString() ?: "",
                participantIds = participantIdsList,
                budget = (travelMap["budget"] as? Number)?.toDouble() ?: 0.0,
                spentAmount = (travelMap["spentAmount"] as? Number)?.toDouble() ?: 0.0,
                imageUrl = travelMap["imageUrl"]?.toString(),
                itinerary = itineraryList,
                createdAt = (travelMap["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun saveTravel(travel: Travel): Result<Unit> {
        return try {
            // Convert Travel to Map for Firebase (handles lists properly)
            val travelMap = mapOf(
                "id" to travel.id,
                "title" to travel.title,
                "description" to (travel.description ?: ""),
                "destination" to travel.destination,
                "startDate" to travel.startDate,
                "endDate" to travel.endDate,
                "organiserId" to travel.organiserId,
                "participantIds" to travel.participantIds, // Firebase handles List<String> as array
                "budget" to travel.budget,
                "spentAmount" to travel.spentAmount,
                "imageUrl" to (travel.imageUrl ?: ""),
                "itinerary" to travel.itinerary.map { item ->
                    mapOf(
                        "id" to item.id,
                        "date" to item.date,
                        "time" to (item.time ?: ""),
                        "title" to item.title,
                        "description" to (item.description ?: ""),
                        "location" to (item.location ?: "")
                    )
                },
                "createdAt" to travel.createdAt
            )
            database.child("travels").child(travel.id).setValue(travelMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTravel(travelId: String): Result<Unit> {
        return try {
            database.child("travels").child(travelId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Activities
    fun observeActivities(travelId: String): Flow<List<Activity>> = callbackFlow {
        val listener = database.child("activities")
            .orderByChild("travelId")
            .equalTo(travelId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activities = snapshot.children.mapNotNull { it.getValue(Activity::class.java) }
                    trySend(activities)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.removeEventListener(listener) }
    }
    
    fun observeAllActivities(): Flow<List<Activity>> = callbackFlow {
        val listener = database.child("activities")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activities = snapshot.children.mapNotNull { it.getValue(Activity::class.java) }
                    trySend(activities)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.removeEventListener(listener) }
    }
    
    fun observeActivity(activityId: String): Flow<Activity?> = callbackFlow {
        val listener = database.child("activities").child(activityId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activity = snapshot.getValue(Activity::class.java)
                    trySend(activity)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.child("activities").child(activityId).removeEventListener(listener) }
    }
    
    suspend fun saveActivity(activity: Activity): Result<Unit> {
        return try {
            database.child("activities").child(activity.id).setValue(activity).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Messages
    fun observeMessages(travelId: String): Flow<List<Message>> = callbackFlow {
        val listener = database.child("travels").child(travelId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                    trySend(messages.sortedBy { it.timestamp })
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.child("travels").child(travelId).child("messages").removeEventListener(listener) }
    }
    
    fun listenToMessagesByTravelId(travelId: String): Flow<List<Message>> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messages = mutableListOf<Message>()
            snapshot.children.forEach { messageSnapshot ->
                val message = messageSnapshot.getValue(Message::class.java)
                if (message != null) {
                    messages.add(message)
                }
            }
            messages.sortBy { it.timestamp }
            trySend(messages)
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    
    // ✅ CORRECT PATH: travels/{travelId}/messages
    database.child("travels").child(travelId).child("messages")
        .addValueEventListener(listener)
    
    awaitClose {
        database.child("travels").child(travelId).child("messages")
            .removeEventListener(listener)
    }
}

    fun saveMessage(message: Message, onResult: (Result<Unit>) -> Unit) {
        database.child("travels")
            .child(message.travelId)
            .child("messages")
            .child(message.id)
            .setValue(message)
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun getMessagesByTravelId(travelId: String, onResult: (Result<List<Message>>) -> Unit) {
        // ✅ CORRECT PATH: travels/{travelId}/messages
        database.child("travels").child(travelId).child("messages")
            .get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val messages = mutableListOf<Message>()
                task.result?.children?.forEach { snapshot ->
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                messages.sortBy { it.timestamp }
                onResult(Result.success(messages))
            } else {
                onResult(Result.failure(task.exception ?: Exception("Unknown error")))
            }
        }
    }
    
    // Budget Items
    fun observeBudgetItems(travelId: String): Flow<List<BudgetItem>> = callbackFlow {
        val listener = database.child("budgetItems")
            .orderByChild("travelId")
            .equalTo(travelId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.mapNotNull { it.getValue(BudgetItem::class.java) }
                    trySend(items)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { database.removeEventListener(listener) }
    }
    
    suspend fun saveBudgetItem(item: BudgetItem): Result<Unit> {
        return try {
            database.child("budgetItems").child(item.id).setValue(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createNotificationForAllUsers(travel: Travel) {
        try {
            val snapshot = database.child("users").get().await()
            
            snapshot.children.forEach { userSnapshot ->
                val userId = userSnapshot.key ?: return@forEach
                
                if (userId == travel.organiserId) {
                    return@forEach
                }
                
                val notificationId = java.util.UUID.randomUUID().toString()
                val notification = Notification(
                    id = notificationId,
                    userId = userId,
                    title = "Nouveau voyage disponible!",
                    message = "${travel.title} à ${travel.destination} - Rejoignez le voyage!",
                    type = NotificationType.TRAVEL_INVITATION,
                    relatedTravelId = travel.id,
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                
                try {
                    database.child("notifications").child(userId).child(notificationId)
                        .setValue(notification).await()
                } catch (e: Exception) {
                    // Handle error silently
                }
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    // Notifications
    fun observeNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = database.child("notifications").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = snapshot.children.mapNotNull { it.getValue(Notification::class.java) }
                    trySend(notifications.sortedByDescending { it.timestamp })
                }
                
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
        
        awaitClose { 
            database.child("notifications").child(userId).removeEventListener(listener)
        }
    }
    
    suspend fun createNotification(notification: Notification): Result<Unit> {
        return try {
            database.child("notifications").child(notification.userId).child(notification.id).setValue(notification).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markNotificationAsRead(userId: String, notificationId: String): Result<Unit> {
        return try {
            database.child("notifications").child(userId).child(notificationId).child("isRead").setValue(true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

