package com.renatoarg.offlinecriptotracker.model.data.local

import android.content.Context
import com.renatoarg.offlinecriptotracker.model.data.local.AppDatabase

object DatabaseProvider {
    @Volatile
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: AppDatabase.getDatabase(context).also { database = it }
        }
    }
}