package com.renatoarg.offlinecriptotracker.model

import com.google.gson.annotations.SerializedName

data class MarketData(
    @SerializedName("current_price")
    val price: Map<String, Double>?,

    @SerializedName("market_cap")
    val market: Map<String, Long>?,

    @SerializedName("total_volume")
    val volume: Map<String, Double>?
)