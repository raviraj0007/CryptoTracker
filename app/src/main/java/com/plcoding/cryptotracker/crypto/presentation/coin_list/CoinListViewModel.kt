package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.crypto.presentation.coin_list.components.CoinListState
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUI
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
): ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            CoinListState()
        )

    private val _event = Channel<CoinListEvent>()
    val events = _event.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when(action) {
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coinUI)
            }
        }
    }

    private fun selectCoin(coinUi: CoinUI) {
        _state.update { it.copy(selectedCoin = coinUi) }

        viewModelScope.launch {
            println("🟡 ViewModel: Loading history for ${coinUi.name}")

            try {
                coinDataSource
                    .getCoinHistory(
                        coinId = coinUi.id,
                        start = ZonedDateTime.now().minusDays(5),
                        end = ZonedDateTime.now()
                    )
                    .onSuccess { history ->
                        println("✅ ViewModel: History loaded (${history.size} points)")
                        val dataPoints = history
                            .sortedBy { it.dataTime }
                            .map {
                                DataPoint(
                                    x = it.dataTime.hour.toFloat(),
                                    y = it.priceUsd.toFloat(),
                                    xLabel = DateTimeFormatter
                                        .ofPattern("ha\nM/d")
                                        .format(it.dataTime)
                                )
                            }

                        _state.update {
                            it.copy(
                                selectedCoin = it.selectedCoin?.copy(
                                    coinPriceHistory = dataPoints
                                )
                            )
                        }
                    }
                    .onError { error ->
                        println("❌ ViewModel: History error - $error")
                        _event.send(CoinListEvent.Error(error))
                    }
            } catch (e: Exception) {
                println("❌ ViewModel: History exception - ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            println("🟡 ViewModel: Starting loadCoins()")

            _state.update { it.copy(
                isLoading = true
            ) }

            try {
                coinDataSource
                    .getCoins()
                    .onSuccess { coins ->
                        println("✅ ViewModel: Success with ${coins.size} coins")
                        _state.update { it.copy(
                            isLoading = false,
                            coins = coins.map { it.toCoinUI() }
                        ) }
                    }
                    .onError { error ->
                        println("❌ ViewModel: Error - $error")
                        _state.update { it.copy(isLoading = false) }
                        _event.send(CoinListEvent.Error(error))
                    }
            } catch (e: Exception) {
                println("❌ ViewModel: Exception caught - ${e.message}")
                e.printStackTrace()
                _state.update { it.copy(isLoading = false) }
                // Optionally send error event
            }
        }
    }
}