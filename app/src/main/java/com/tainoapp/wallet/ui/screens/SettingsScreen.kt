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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tainoapp.wallet.BuildConfig
import com.tainoapp.wallet.WalletApplication
import com.tainoapp.wallet.data.BudgetCycle
import com.tainoapp.wallet.ui.screens.settings.AboutDialog
import com.tainoapp.wallet.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen() {
    val application = LocalContext.current.applicationContext as WalletApplication
    val repository = application.userPreferencesRepository

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.provideFactory(repository)
    )

    val selectedCycle by settingsViewModel.budgetCycle.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }

    SettingsScreenContent(
        selectedCycle = selectedCycle,
        onCycleSelected = { newCycle ->
            settingsViewModel.updateBudgetCycle(newCycle)
        },
        onAboutClick = { showAboutDialog = true }
    )

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }
}

@Composable
fun SettingsScreenContent(
    selectedCycle: BudgetCycle,
    onCycleSelected: (BudgetCycle) -> Unit,
    onAboutClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // Budget Cycle Selection
        Column {
            Text("Budget Cycle", style = MaterialTheme.typography.titleLarge)
            Text("Select how often your budget resets.", style = MaterialTheme.typography.bodyMedium)

            BudgetCycle.entries.forEach { cycle ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCycleSelected(cycle) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (cycle == selectedCycle),
                        onClick = { onCycleSelected(cycle) }
                    )
                    Text(
                        text = cycle.name.replaceFirstChar { it.titlecase() }.replace("_", " "),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // About Section
        Column(modifier = Modifier.clickable(onClick = onAboutClick)) {
            Text("About", style = MaterialTheme.typography.titleLarge)
            Text("App Version: ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
