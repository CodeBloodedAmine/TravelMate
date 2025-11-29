package com.example.travelmate

import android.app.Application
import com.example.travelmate.data.room.TravelMateDatabase

class TravelMateApplication : Application() {
    val database by lazy { TravelMateDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: TravelMateApplication
            private set
    }
}

