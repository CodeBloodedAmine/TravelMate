package com.example.travelmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelmate.TravelMateApplication
import com.example.travelmate.data.repository.*

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val travelRepository: TravelRepositoryHybrid,
    private val activityRepository: ActivityRepositoryHybrid,
    private val budgetRepository: BudgetRepositoryHybrid,
    private val messageRepository: MessageRepositoryHybrid,
    private val notificationRepository: NotificationRepositoryHybrid,
    private val firebaseAuthService: com.example.travelmate.data.firebase.FirebaseAuthService
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(firebaseAuthService, userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(travelRepository) as T
            }
            modelClass.isAssignableFrom(TravelViewModel::class.java) -> {
                TravelViewModel(travelRepository) as T
            }
            modelClass.isAssignableFrom(ActivityViewModel::class.java) -> {
                ActivityViewModel(activityRepository) as T
            }
            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> {
                BudgetViewModel(budgetRepository, travelRepository) as T
            }
            modelClass.isAssignableFrom(MessageViewModel::class.java) -> {
                MessageViewModel(messageRepository) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(notificationRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    
    companion object {
        fun create(context: android.content.Context): ViewModelFactory {
            val app = context.applicationContext as TravelMateApplication
            val database = app.database
            
            return ViewModelFactory(
                userRepository = UserRepository(database.userDao()),
                travelRepository = TravelRepositoryHybrid(
                    database.travelDao(),
                    app.firebaseRealtimeService,
                    app.networkMonitor
                ),
                activityRepository = ActivityRepositoryHybrid(
                    database.activityDao(),
                    app.firebaseRealtimeService,
                    app.networkMonitor
                ),
                budgetRepository = BudgetRepositoryHybrid(
                    database.budgetDao(),
                    app.firebaseRealtimeService,
                    app.networkMonitor
                ),
                messageRepository = MessageRepositoryHybrid(
                    database.messageDao(),
                    app.firebaseRealtimeService,
                    app.networkMonitor
                ),
                notificationRepository = NotificationRepositoryHybrid(
                    database.notificationDao(),
                    app.firebaseRealtimeService,
                    app.networkMonitor
                ),
                firebaseAuthService = app.firebaseAuthService
            )
        }
    }
}
