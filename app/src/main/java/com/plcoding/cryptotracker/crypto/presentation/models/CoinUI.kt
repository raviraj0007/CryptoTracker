package com.plcoding.cryptotracker.crypto.presentation.models

import androidx.annotation.DrawableRes
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.core.presentation.util.getDrawableIdForCoin
import java.util.Locale.getDefault


//Data class for horizontal coin in coin list
data class CoinUI(
    val id: String,
    val rank: Int,
    val symbol: String,
    val name: String,
    val marketCapUsd: DisplayableNumber,
    val priceUsd: DisplayableNumber,
    val changePercent24h: DisplayableNumber,
    @DrawableRes val iconRes:Int
)




data class DisplayableNumber(
    val value: Double,
    val formatted: String
)

fun Coin.toCoinUI(): CoinUI{
    return CoinUI(
        id = id,
        rank = rank,
        symbol = symbol,
        name = name,
        marketCapUsd = marketCapUsd.toDisplayableNumber(),
        priceUsd = priceUsd.toDisplayableNumber(),
        changePercent24h = changePercent24Hr.toDisplayableNumber(),
        iconRes = getDrawableIdForCoin(symbol)
    )
}


fun Double.toDisplayableNumber(): DisplayableNumber {
    val formatter = android.icu.text.NumberFormat.getNumberInstance(getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return DisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )

}