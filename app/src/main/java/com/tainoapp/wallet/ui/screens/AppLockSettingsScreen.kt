package com.tainoapp.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.WalletApplication
import com.tainoapp.wallet.repository.AppLockSettings
import com.tainoapp.wallet.util.AppLockAuthentication
import kotlinx.coroutines.launch

@Composable
fun AppLockSettingsScreen() {
    val context = LocalContext.current
    val repository =
        (context.applicationContext as WalletApplication).userPreferencesRepository
    val coroutineScope = rememberCoroutineScope()
    val appLockSettings by repository.appLockSettings.collectAsState(
        initial = AppLockSettings(enabled = false, lastBackgroundTime = null)
    )
    var appLockError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("App Lock", style = MaterialTheme.typography.headlineMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Require authentication")
                Text(
                    "Require authentication at startup and after Wallet has been away for one minute. Use biometrics or your device screen lock.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = appLockSettings.enabled,
                onCheckedChange = { enabled ->
                    appLockError = false
                    if (!enabled || AppLockAuthentication.isAvailable(context)) {
                        coroutineScope.launch {
                            repository.setAppLockEnabled(enabled)
                        }
                    } else {
                        appLockError = true
                    }
                }
            )
        }
        if (appLockError) {
            Text(
                "Set up biometrics or a secure device screen lock to enable app lock.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
