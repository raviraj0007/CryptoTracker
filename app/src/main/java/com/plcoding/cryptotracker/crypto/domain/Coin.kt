package com.plcoding.cryptotracker.crypto.domain

data class Coin(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24h: Double,
)