package com.renatoarg.offlinecriptotracker.model

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val desc: Map<String, String>,

    @SerializedName("image")
    val image: CoinImage,

    @SerializedName("market_data")
    val marketData: MarketData?
)