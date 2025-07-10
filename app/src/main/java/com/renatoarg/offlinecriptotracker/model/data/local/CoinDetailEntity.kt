package com.renatoarg.offlinecriptotracker.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "details")
data class CoinDetailEntity(
    @PrimaryKey
    val id: String,
    val symbol: String,
    val name: String,
    val description: String?,
    val image: String?,
    val price: Double?
)