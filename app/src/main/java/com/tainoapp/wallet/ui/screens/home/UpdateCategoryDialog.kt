package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.ui.theme.WalletTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * A dialog for updating an existing budget category.
 * @param category The category being updated.
 * @param onDismissRequest Action for when the dialog is dismissed.
 * @param onConfirm Action to confirm changes. Passes the amount to add and the new target.
 * @param onClear Action to clear the category's spending.
 */
@Composable
fun UpdateCategoryDialog(
    category: BudgetCategory,
    onDismissRequest: () -> Unit,
    onConfirm: (amountToAdd: Float, newTarget: Float) -> Unit,
    onClear: () -> Unit
) {
    val initialTargetText = if (category.target == 0f) "" else category.target.toString()
    var amountToAdd by remember { mutableStateOf("") }
    var newTarget by remember { mutableStateOf(initialTargetText) }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Update ${category.name}") },
        text = {
            Column {
                Text(
                    "Current: ${currencyFormat.format(category.currentAmount)} / ${currencyFormat.format(category.target)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountToAdd,
                    onValueChange = { amountToAdd = it },
                    label = { Text("Amount to Add") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newTarget,
                    onValueChange = { newTarget = it },
                    label = { Text("Edit Target") }, // Corrected Text
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val addAmount = amountToAdd.toFloatOrNull() ?: 0f
                    val targetAmount = newTarget.toFloatOrNull() ?: 0f // Treat empty as 0
                    onConfirm(addAmount, targetAmount)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(
                    onClick = onClear,
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UpdateCategoryDialogPreview() {
    WalletTheme {
        UpdateCategoryDialog(
            category = BudgetCategory("Groceries", 400f, 150f, Color.Green.value),
            onDismissRequest = {},
            onConfirm = { _, _ -> },
            onClear = {}
        )
    }
}
