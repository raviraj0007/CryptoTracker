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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime

class RemoteCoinDataSource(
    private val httpClient: HttpClient
) : CoinDataSource {

    // ✅ CRITICAL FIX: Wrap the ENTIRE function including .map in withContext
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> = withContext(Dispatchers.IO) {
        println("🔵 getCoins() called on thread: ${Thread.currentThread().name}")

        val result = safeCall<CoinsResponseDto> {
            httpClient.get(constructUrl("/assets")) {
                headers {
                    append("Authorization", "Bearer ${BuildConfig.API_KEY}")
                }
            }
        }

        // Map operation now also happens on IO thread
        result.map { response ->
            println("✅ API Success: ${response.data.size} coins received")
            response.data.map { it.toCoin() }
        }.also { finalResult ->
            println("✅ Final getCoins() Result type: ${finalResult::class.simpleName}")
            when (finalResult) {
                is Result.Success -> println("   Success with ${finalResult.data.size} coins")
                is Result.Error -> println("   Error: ${finalResult.error}")
            }
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> = withContext(Dispatchers.IO) {
        println("🔵 getCoinHistory() called for $coinId on thread: ${Thread.currentThread().name}")

        val startMillis = start
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        val endMillis = end
            .withZoneSameInstant(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()

        val result = safeCall<CoinHistoryDto> {
            httpClient.get(constructUrl("/assets/$coinId/history")) {
                headers {
                    append("Authorization", "Bearer ${BuildConfig.API_KEY}")
                }
                parameter("interval", "h6")
                parameter("start", startMillis)
                parameter("end", endMillis)
            }
        }

        result.map { response ->
            response.data.map { it.toCoinPrice() }
        }
    }
}