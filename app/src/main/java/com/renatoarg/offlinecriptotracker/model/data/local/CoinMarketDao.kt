package com.renatoarg.offlinecriptotracker.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinMarketDao {

    @Query("SELECT * FROM coins ORDER BY name ASC")
    fun getAllCoinsFlow(): Flow<List<CoinEntity>>

    @Query("SELECT * FROM coins ORDER BY name ASC")
    suspend fun getAllCoins(): List<CoinEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoins(coins: List<CoinEntity>)

    @Query("DELETE FROM coins")
    suspend fun deleteAllCoins()

    @Transaction
    suspend fun replaceAllCoins(coins: List<CoinEntity>) {
        deleteAllCoins()
        insertCoins(coins)
    }
}