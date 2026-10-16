package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.tainoapp.wallet.ui.theme.WalletTheme

/**
 * A dialog for editing the total balance.
 * @param currentBalance The current balance to pre-fill the text field.
 * @param onDismissRequest The action to perform when the dialog is dismissed.
 * @param onConfirm The action to perform when the user confirms the new balance.
 */
@Composable
fun EditBalanceDialog(
    currentBalance: Float,
    onDismissRequest: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    // If the balance is 0, start with an empty field. Otherwise, show the current balance.
    val initialText = if (currentBalance == 0f) "" else currentBalance.toString()
    var text by remember { mutableStateOf(initialText) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Edit Balance") },
        text = {
            Column {
                Text("Enter your new total balance below.")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("New Balance") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // If the field is empty, treat it as 0.
                    val newBalance = text.toFloatOrNull() ?: 0f
                    onConfirm(newBalance)
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditBalanceDialogPreview() {
    WalletTheme {
        EditBalanceDialog(
            currentBalance = 1234.56f,
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}
