package com.plcoding.cryptotracker.crypto.data.Networking

import com.plcoding.cryptotracker.BuildConfig
import com.plcoding.cryptotracker.core.data.networking.constructUrl
import com.plcoding.cryptotracker.core.data.networking.safeCall
import com.plcoding.cryptotracker.core.domain.util.NetworkError
import com.plcoding.cryptotracker.core.domain.util.Result
import com.plcoding.cryptotracker.core.domain.util.map
import com.plcoding.cryptotracker.crypto.data.Networking.dto.CoinHistoryDto
import com.plcoding.cryptotracker.crypto.data.mappers.toCoin
import com.plcoding.cryptotracker.crypto.data.mappers.toCoinPrice
import com.plcoding.cryptotracker.crypto.data.Networking.dto.CoinsResponseDto
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.domain.CoinPrice
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinsResponseDto> {
            httpClient.get(constructUrl("/assets")) {
                headers {
                    append("Authorization", "Bearer ${BuildConfig.API_KEY}")
                }
            }
        }.map { response ->
            println("✅ API Success: ${response.data.size} coins received")
            response.data.map { it.toCoin() }
        }.also {
            println("✅ Final getCoins() Result: $it")
        }
    }


    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> {
        val startMillis = start
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        val endMillis = end
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        return safeCall<CoinHistoryDto> {
            httpClient.get(constructUrl("/assets/$coinId/history")) {
                headers {
                    append("Authorization", "Bearer ${BuildConfig.API_KEY}")
                }
                parameter("interval", "h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
            }

        }.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }
}