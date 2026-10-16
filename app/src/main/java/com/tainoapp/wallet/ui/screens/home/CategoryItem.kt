package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.ui.theme.WalletTheme

private fun formatAmount(amount: Float): String {
    return if (amount == amount.toInt().toFloat()) {
        amount.toInt().toString()
    } else {
        String.format("%.2f", amount)
    }
}

@Composable
fun CategoryItem(
    category: BudgetCategory,
    isRemoveMode: Boolean,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    val progress = if (category.target > 0) category.currentAmount / category.target else 0f

    val shouldShowIcons = category.target > 0f && category.currentAmount >= category.target
    val showWarning = shouldShowIcons && category.name !in listOf("Mortgage", "Savings")
    val showSatisfied = shouldShowIcons && category.name == "Savings"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isRemoveMode, onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(category.colorValue))
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = category.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${formatAmount(category.currentAmount)} / ${formatAmount(category.target)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = Color(category.colorValue),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        if (isRemoveMode) {
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Category", tint = MaterialTheme.colorScheme.error)
            }
        } else {
            if (showWarning) {
                Icon(Icons.Default.Warning, contentDescription = "Target exceeded", tint = Color.Yellow)
            } else if (showSatisfied) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = "Target met", tint = Color.Green)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    WalletTheme {
        Column {
            CategoryItem(
                category = BudgetCategory("Groceries", 400f, 150f, Color(0xFF4CAF50).value),
                isRemoveMode = false,
                onClick = {},
                onRemoveClick = {}
            )
            CategoryItem(
                category = BudgetCategory("Gas", 150f, 160f, Color(0xFFFF9800).value),
                isRemoveMode = true,
                onClick = {},
                onRemoveClick = {}
            )
        }
    }
}
