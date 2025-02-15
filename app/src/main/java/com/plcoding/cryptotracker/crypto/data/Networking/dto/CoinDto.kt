package com.plcoding.cryptotracker.crypto.data.Networking.dto

import kotlinx.serialization.Serializable


//Data Transfer Object
//we can do that in coin.kt class ,that is violation of our Architecture

@Serializable
data class CoinDto(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double,
)
