package com.plcoding.cryptotracker.crypto.data.Networking.dto

import kotlinx.serialization.Serializable


@Serializable
data class CoinsResponseDto(
    val data: List<CoinDto>
)
