package com.tainoapp.wallet.ui.screens.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.util.DateUtils
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryItem(periodKey: String, categories: List<BudgetCategory>) {
    val totalSpent = categories.sumOf { it.currentAmount.toDouble() }.toFloat()
    val totalTarget = categories.sumOf { it.target.toDouble() }.toFloat()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = DateUtils.parsePeriodKey(periodKey),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row {
            Text("Total Spent:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(totalSpent))
        }
        Row {
            Text("Total Target:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(totalTarget))
        }
    }
}
