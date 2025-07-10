package com.renatoarg.offlinecriptotracker.model.api

import com.renatoarg.offlinecriptotracker.model.CoinDetail
import com.renatoarg.offlinecriptotracker.model.Coin
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApi {
    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") currency: String = "usd",
    ): List<Coin>

    @GET("coins/{id}")
    suspend fun getDetails(
        @Path("id") coinId: String,
        @Query("market_data") marketData: Boolean = true,
    ): CoinDetail
}