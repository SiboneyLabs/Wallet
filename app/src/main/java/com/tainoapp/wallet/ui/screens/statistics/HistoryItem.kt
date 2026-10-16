package com.tainoapp.wallet.ui.screens.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.ui.viewmodels.PeriodHistory
import com.tainoapp.wallet.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryItem(periodKey: String, history: PeriodHistory) {
    val totalSpent = history.categories.sumOf { it.currentAmount.toDouble() }.toFloat()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = DateUtils.parsePeriodKey(periodKey),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Text("Total Added Funds:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(currencyFormat.format(history.totalAddedFunds))
        }
        Row {
            Text("Total Spent:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(currencyFormat.format(totalSpent))
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Detailed category breakdown
        Column(modifier = Modifier.padding(start = 8.dp)) {
            history.categories.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${it.name}:", modifier = Modifier.weight(1f))
                    Text(text = currencyFormat.format(it.currentAmount), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
