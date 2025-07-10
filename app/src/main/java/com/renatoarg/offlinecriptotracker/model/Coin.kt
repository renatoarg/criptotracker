package com.renatoarg.offlinecriptotracker.model

import com.google.gson.annotations.SerializedName

data class Coin(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("current_price")
    val price: Double?
)