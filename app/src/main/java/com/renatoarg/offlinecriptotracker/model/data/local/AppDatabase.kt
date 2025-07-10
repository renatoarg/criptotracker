package com.renatoarg.offlinecriptotracker.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CoinEntity::class,
        CoinDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinMarketDao(): CoinMarketDao
    abstract fun coinDetailDao(): CoinDetailDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coindb"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}