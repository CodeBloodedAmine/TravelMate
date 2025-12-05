package com.example.travelmate.data.firebase

import android.util.Log
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
                    val travels = snapshot.children.mapNotNull { it.getValue(Travel::class.java) }
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
                    val travels = snapshot.children.mapNotNull { it.getValue(Travel::class.java) }
                    Log.d("FirebaseRealtimeService", "Loaded ${travels.size} travels from Firebase")
                    trySend(travels)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseRealtimeService", "Error observing travels: ${error.message}")
                    close(error.toException())
                }
            })
        
        awaitClose { database.child("travels").removeEventListener(listener) }
    }
    
    fun observeTravel(travelId: String): Flow<Travel?> = callbackFlow {
        val listener = database.child("travels").child(travelId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val travel = snapshot.getValue(Travel::class.java)
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
            snapshot.getValue(Travel::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRealtimeService", "Error getting travel: ${e.message}")
            null
        }
    }
    
    suspend fun saveTravel(travel: Travel): Result<Unit> {
        return try {
            database.child("travels").child(travel.id).setValue(travel).await()
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
        // ✅ CORRECT PATH: travels/{travelId}/messages/{messageId}
        val path = "travels/${message.travelId}/messages/${message.id}"
        Log.d("FirebaseRealtimeService", "🚀 Saving message to: $path")
        Log.d("FirebaseRealtimeService", "Message data: id=${message.id}, travelId=${message.travelId}, content=${message.content}")
        
        database.child("travels")
            .child(message.travelId)
            .child("messages")
            .child(message.id)
            .setValue(message)
            .addOnSuccessListener {
                Log.d("FirebaseRealtimeService", "✅ Message saved successfully to: $path")
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseRealtimeService", "❌ Failed to save message to: $path")
                Log.e("FirebaseRealtimeService", "Error: ${exception.message}")
                exception.printStackTrace()
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
            Log.d("FirebaseRealtimeService", "Creating notifications for new travel: ${travel.title}")
            
            // Get all users
            database.child("users").get().addOnSuccessListener { snapshot ->
                snapshot.children.forEach { userSnapshot ->
                    val userId = userSnapshot.key ?: return@forEach
                    
                    // Don't notify the organizer (they already know)
                    if (userId == travel.organiserId) return@forEach
                    
                    // Create notification for this user
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
                    
                    // Save to Firebase: /notifications/{userId}/{notificationId}
                    database.child("notifications").child(userId).child(notificationId)
                        .setValue(notification)
                        .addOnSuccessListener {
                            Log.d("FirebaseRealtimeService", "✅ Notification created for user $userId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseRealtimeService", "❌ Failed to create notification: ${e.message}")
                        }
                }
            }.addOnFailureListener { e ->
                Log.e("FirebaseRealtimeService", "Error getting users: ${e.message}")
            }
        } catch (e: Exception) {
            Log.e("FirebaseRealtimeService", "Error creating notifications: ${e.message}")
            e.printStackTrace()
        }
    }
    
    // Notifications
    fun observeNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = database.child("notifications").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = snapshot.children.mapNotNull { it.getValue(Notification::class.java) }
                    Log.d("FirebaseRealtimeService", "📬 Loaded ${notifications.size} notifications for user $userId")
                    trySend(notifications.sortedByDescending { it.timestamp })
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseRealtimeService", "❌ Error loading notifications: ${error.message}")
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

