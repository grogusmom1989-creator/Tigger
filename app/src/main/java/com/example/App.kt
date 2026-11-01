package com.example

import android.app.Application

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    lateinit var database: com.example.data.room.KikiDatabase
    lateinit var sparkleDatabase: com.example.data.room.OneDirectionSparkleDatabase
    lateinit var sparkleRepository: com.example.data.SparkleRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = androidx.room.Room.databaseBuilder(
            this,
            com.example.data.room.KikiDatabase::class.java,
            "kiki-database"
        ).build()
        sparkleDatabase = com.example.data.room.OneDirectionSparkleDatabase.getDatabase(this)
        sparkleRepository = com.example.data.SparkleRepository(this, sparkleDatabase.sparkleDao())
    }
}
