package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.crypto.presentation.models.CoinUI

sealed interface CoinListAction {
    data class OnCoinClick(val coinUI: CoinUI): CoinListAction
    data object OnBackClick: CoinListAction // ✅ Must be here
}