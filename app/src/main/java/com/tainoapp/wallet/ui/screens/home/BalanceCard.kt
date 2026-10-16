package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.ui.theme.WalletTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * A card that displays the user's total balance.
 * @param balance The total balance to display.
 * @param onClick The action to perform when the card is tapped.
 */
@Composable
fun BalanceCard(balance: Float, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BalanceCardPreview() {
    WalletTheme {
        BalanceCard(balance = 1234.56f, onClick = {})
    }
}
