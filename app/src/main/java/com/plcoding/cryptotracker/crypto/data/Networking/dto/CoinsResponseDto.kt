package com.plcoding.cryptotracker.crypto.data.Networking.dto

import com.plcoding.cryptotracker.crypto.data.Networking.dto.CoinDto
import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: List<CoinDto>
)