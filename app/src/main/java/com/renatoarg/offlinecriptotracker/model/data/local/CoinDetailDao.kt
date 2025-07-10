package com.renatoarg.offlinecriptotracker.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDetailDao {

    @Query("SELECT * FROM details WHERE id = :coinId")
    fun getDetails(coinId: String): Flow<CoinDetailEntity?>

    @Query("SELECT * FROM details WHERE id = :coinId")
    suspend fun getDetail(coinId: String): CoinDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoinDetail(coinDetail: CoinDetailEntity)
}