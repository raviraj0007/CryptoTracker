package com.plcoding.cryptotracker.crypto.domain
//its data class hold data
//its what we get from api
data class Coin(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double,
)