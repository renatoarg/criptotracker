package com.renatoarg.offlinecriptotracker.model.data.local

import com.renatoarg.offlinecriptotracker.model.CoinDetail
import com.renatoarg.offlinecriptotracker.model.CoinImage
import com.renatoarg.offlinecriptotracker.model.Coin
import com.renatoarg.offlinecriptotracker.model.MarketData

fun Coin.toEntity(): CoinEntity {
    return CoinEntity(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        image = this.image,
        price = this.price
    )
}

fun CoinEntity.toModel(): Coin {
    return Coin(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        image = this.image,
        price = this.price
    )
}

fun CoinDetail.toEntity(): CoinDetailEntity {
    return CoinDetailEntity(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        description = this.desc["en"],
        image = this.image.large ?: this.image.small ?: this.image.thumb,
        price = this.marketData?.price?.get("usd")
    )
}

fun CoinDetailEntity.toModel(): CoinDetail {
    return CoinDetail(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        desc = mapOf("en" to (this.description ?: "")),
        image = CoinImage(
            thumb = this.image,
            small = this.image,
            large = this.image
        ),
        marketData = MarketData(
            price = this.price?.let { mapOf("usd" to it) },
            market = null,
            volume = null
        )
    )
}