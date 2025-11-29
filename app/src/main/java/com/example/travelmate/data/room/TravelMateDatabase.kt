package com.example.travelmate.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.travelmate.data.models.*

@Database(
    entities = [
        User::class,
        Travel::class,
        Activity::class,
        BudgetItem::class,
        Message::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TravelMateDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun travelDao(): TravelDao
    abstract fun activityDao(): ActivityDao
    abstract fun budgetDao(): BudgetDao
    abstract fun messageDao(): MessageDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: TravelMateDatabase? = null

        fun getDatabase(context: Context): TravelMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelMateDatabase::class.java,
                    "travelmate_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

