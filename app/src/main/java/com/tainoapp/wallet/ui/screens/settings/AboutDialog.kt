package com.tainoapp.wallet.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import com.tainoapp.wallet.BuildConfig

@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("About Wallet") },
        text = {
            Column {
                Text("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                Text("Created by SiboneyLabs")
                TextButton(onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }) {
                    Text("Privacy Policy")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        }
    )
}
