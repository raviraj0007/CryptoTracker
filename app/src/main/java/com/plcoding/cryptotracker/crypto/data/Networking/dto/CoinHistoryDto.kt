package com.plcoding.cryptotracker.crypto.data.Networking.dto

import com.plcoding.cryptotracker.crypto.data.networking.dto.CoinPriceDto
import kotlinx.serialization.Serializable


@Serializable
data class CoinHistoryDto(
    val data: List<CoinPriceDto>
)
