package com.example.goldameirl.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.goldameirl.model.Branch
import com.example.goldameirl.model.Alert

@Database(entities = [Alert::class, Branch::class], version = 3, exportSchema = false)
abstract class DB : RoomDatabase() {
    abstract val alertDAO: AlertDAO
    abstract val branchDAO: BranchDAO

    companion object {
        @Volatile
        private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB? {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, DB::class.java, "app_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}