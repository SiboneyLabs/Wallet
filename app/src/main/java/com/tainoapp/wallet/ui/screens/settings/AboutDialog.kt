package com.tainoapp.wallet.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.tainoapp.wallet.BuildConfig

@Composable
fun AboutDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("About Wallet") },
        text = {
            Column {
                Text("Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                Text("Created by Taino App")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        }
    )
}
