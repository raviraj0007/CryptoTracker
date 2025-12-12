package com.plcoding.cryptotracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.cryptotracker.core.presentation.util.ObserveAsEvents
import com.plcoding.cryptotracker.core.presentation.util.toString
import com.plcoding.cryptotracker.crypto.presentation.coin_detail.CoinDetailScreen
import com.plcoding.cryptotracker.crypto.presentation.coin_list.CoinListEvent
import com.plcoding.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import com.plcoding.cryptotracker.crypto.presentation.coin_list.components.CoinListScreen
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme
import org.koin.androidx.compose.koinViewModel;

/* Summary
Loads with a coin list screen.
If a coin is picked, shows the coin's detail screen.
Handles errors by showing a message ("toast").
*/

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = koinViewModel<CoinListViewModel>() // Inject the ViewModel
                    val state by viewModel.state.collectAsStateWithLifecycle() // Observe the state
                    val context = LocalContext.current // Get the context
                    ObserveAsEvents(events = viewModel.events) { event -> // Observe events
                        when(event) {
                            is CoinListEvent.Error -> { // Handle the error event
                                Toast.makeText(
                                    context,
                                    event.error.toString(context),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    when{ // Render the appropriate screen based on the current state
                        state.selectedCoin != null -> {
                            CoinDetailScreen( // Call the coin detail screen with parameters
                                state = state,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                       else -> { // Render the coin list screen
                           CoinListScreen( // Call the coin list screen with parameters
                               state = state,
                               modifier = Modifier.padding(innerPadding),
                               onAction = viewModel::onAction // Pass the action handler
                           )
                       }
                    }
                }
            }
        }
    }
}