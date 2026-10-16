package com.tainoapp.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppLockScreen(
    error: String?,
    onRetry: () -> Unit,
    isLoading: Boolean = false
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isLoading) "Loading Wallet..." else "Wallet is locked",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                if (isLoading) "Checking app lock settings." else "Authenticate to continue.",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            if (error != null && !isLoading) {
                Text(
                    error,
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Try again")
                }
            }
        }
    }
}
