package com.renatoarg.offlinecriptotracker.model

import android.content.Context
import com.renatoarg.offlinecriptotracker.model.api.ApiClient
import com.renatoarg.offlinecriptotracker.model.data.local.DatabaseProvider
import com.renatoarg.offlinecriptotracker.model.data.local.toEntity
import com.renatoarg.offlinecriptotracker.model.data.local.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CoinRepository(private val context: Context) {
    private val api = ApiClient.coinApi
    private val database = DatabaseProvider.getDatabase(context)
    private val coinMarketDao = database.coinMarketDao()
    private val coinDetailDao = database.coinDetailDao()

    // Cache-first
    fun getCoinMarketsFlow(): Flow<List<Coin>> {
        println("-=> getCoinMarketsFlow:")
        return coinMarketDao.getAllCoinsFlow().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun refreshCoinMarkets(): Result<Unit> {
        return try {
            println("-=> refreshCoinMarket:")
            val apiCoins = api.getMarkets()
            println("-=> ${apiCoins.size} coins")

            val entities = apiCoins.map { it.toEntity() }
            coinMarketDao.replaceAllCoins(entities)
            println("-=> coins replaced in room")

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            Result.failure(e)
        }
    }


    fun getCoinDetailFlow(coinId: String): Flow<CoinDetail?> {
        println("-=> getCoinDetailFlow:")
        return coinDetailDao.getDetails(coinId).map { entity ->
            entity?.toModel()
        }
    }

    suspend fun refreshCoinDetail(coinId: String): Result<Unit> {
        return try {
            println("-=> refreshCoinDetail:")
            val apiCoinDetail = api.getDetails(coinId)
            val entity = apiCoinDetail.toEntity()
            coinDetailDao.insertCoinDetail(entity)
            println("-=> coin detail insert")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}