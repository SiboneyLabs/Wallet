package com.tainoapp.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tainoapp.wallet.WalletApplication
import com.tainoapp.wallet.ui.screens.statistics.HistoryItem
import com.tainoapp.wallet.ui.viewmodels.StatisticsViewModel

@Composable
fun StatisticsScreen() {
    val application = LocalContext.current.applicationContext as WalletApplication
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModel.provideFactory(application.userPreferencesRepository)
    )

    val uiState by statisticsViewModel.uiState.collectAsState()

    if (uiState.relevantHistory.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No History Yet", style = MaterialTheme.typography.headlineMedium)
            Text("Complete a budget period to see your history here.", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Budget History", style = MaterialTheme.typography.headlineMedium)
            }
            items(uiState.relevantHistory.toList()) { (periodKey, history) ->
                HistoryItem(periodKey = periodKey, history = history)
            }
        }
    }
}
