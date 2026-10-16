package com.tainoapp.wallet.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import com.tainoapp.wallet.data.BudgetCycle
import com.tainoapp.wallet.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen() {
    val application = LocalContext.current.applicationContext as WalletApplication
    val repository = application.userPreferencesRepository

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.provideFactory(repository)
    )

    val selectedCycle by settingsViewModel.budgetCycle.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Budget Cycle", style = MaterialTheme.typography.headlineMedium)
        Text("Select how often your budget resets.", style = MaterialTheme.typography.bodyLarge)

        BudgetCycle.entries.forEach { cycle ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { settingsViewModel.updateBudgetCycle(cycle) }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (cycle == selectedCycle),
                    onClick = { settingsViewModel.updateBudgetCycle(cycle) }
                )
                Text(
                    text = cycle.name.replaceFirstChar { it.titlecase() }.replace("_", " "),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
