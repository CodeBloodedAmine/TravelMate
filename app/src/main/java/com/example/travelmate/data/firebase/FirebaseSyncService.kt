package com.example.travelmate.data.firebase

import com.example.travelmate.data.models.*
import com.example.travelmate.data.room.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service pour synchroniser les données Firebase avec Room Database locale
 * Cela permet d'avoir les données disponibles même en mode offline
 */
class FirebaseSyncService(
    private val travelDao: TravelDao,
    private val activityDao: ActivityDao,
    private val messageDao: MessageDao,
    private val budgetDao: BudgetDao,
    private val notificationDao: NotificationDao
) {
    private val database = Firebase.database.reference
    private val scope = CoroutineScope(Dispatchers.IO)
    
    fun startSyncing(userId: String) {
        syncTravels(userId)
        syncActivities(userId)
        syncMessages(userId)
        syncBudgetItems(userId)
        syncNotifications(userId)
    }
    
    private fun syncTravels(userId: String) {
        database.child("travels")
            .orderByChild("organiserId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scope.launch {
                        snapshot.children.forEach { child ->
                            val travel = child.getValue(Travel::class.java)
                            travel?.let { travelDao.insertTravel(it) }
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    
    private fun syncActivities(userId: String) {
        // Sync activities for all travels of the user
        database.child("activities")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scope.launch {
                        snapshot.children.forEach { child ->
                            val activity = child.getValue(Activity::class.java)
                            activity?.let { activityDao.insertActivity(it) }
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    
    private fun syncMessages(userId: String) {
        database.child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scope.launch {
                        snapshot.children.forEach { child ->
                            val message = child.getValue(Message::class.java)
                            message?.let { messageDao.insertMessage(it) }
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    
    private fun syncBudgetItems(userId: String) {
        database.child("budgetItems")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scope.launch {
                        snapshot.children.forEach { child ->
                            val item = child.getValue(BudgetItem::class.java)
                            item?.let { budgetDao.insertBudgetItem(it) }
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
    
    private fun syncNotifications(userId: String) {
        database.child("notifications")
            .orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scope.launch {
                        snapshot.children.forEach { child ->
                            val notification = child.getValue(Notification::class.java)
                            notification?.let { notificationDao.insertNotification(it) }
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}

