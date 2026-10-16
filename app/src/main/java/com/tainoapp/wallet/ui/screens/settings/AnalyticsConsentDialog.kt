package com.tainoapp.wallet.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

const val PRIVACY_POLICY_URL = "https://siboneylabs.github.io/Wallet/"

@Composable
fun AnalyticsConsentDialog(onChoice: (Boolean) -> Unit) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = { onChoice(false) },
        title = { Text("Help improve Wallet?") },
        text = {
            Column {
                Text(
                    "If you allow, Wallet will share app and device usage information with Google Analytics. " +
                        "Your balance, budgets, categories, and history stay on this device and are not sent " +
                        "through analytics. You can change this choice later in Settings."
                )
                TextButton(onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }) {
                    Text("Read privacy policy")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onChoice(true) }) {
                Text("Allow analytics")
            }
        },
        dismissButton = {
            TextButton(onClick = { onChoice(false) }) {
                Text("No thanks")
            }
        }
    )
}
