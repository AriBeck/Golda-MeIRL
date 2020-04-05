package com.example.goldameirl.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goldameirl.model.Notification

@Database(entities = [Notification::class], version = 1, exportSchema = false)
abstract class DB: RoomDatabase() {
    abstract val notificationDAO: NotificationDAO

    companion object {
        @Volatile
        private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB? {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, DB::class.java, "app_database"
                    )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}