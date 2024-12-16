package com.plcoding.cryptotracker.crypto.presentation.coin_list.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUI
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUI
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme

@Composable
fun CoinListItem(
    coinUI: CoinUI,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if(isSystemInDarkTheme()){
        androidx.compose.ui.graphics.Color.White
    } else{
        androidx.compose.ui.graphics.Color.Black
    }

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = coinUI.iconRes),
            contentDescription = coinUI.name,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(85.dp)

        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = coinUI.symbol,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$${coinUI.priceUsd.formatted}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor)

            Spacer(modifier = Modifier.height(8.dp))
            PriceChange(
                change = coinUI.changePercent24h
            )
        }

    }
}


@PreviewLightDark
@Composable
private fun CoinListItemPreview() {
    CryptoTrackerTheme {
        CoinListItem(
            coinUI = previewCoin ,
            onClick = { /*TODO*/ },
            modifier = Modifier.background(
                MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

internal val previewCoin = Coin(
    id = "bitcoin",
    rank = 1,
    symbol = "BTC",
    name = "Bitcoin",
    marketCapUsd = 123456789.0,
    priceUsd = 12345.67,
    changePercent24h = 0.1
).toCoinUI()














