package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.ui.theme.WalletTheme

/**
 * A dialog for adding a new budget category.
 * @param onDismissRequest The action to perform when the dialog is dismissed.
 * @param onConfirm The action to perform when the user confirms the new category.
 */
@Composable
fun AddCategoryDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (name: String, target: Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Add New Category") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Monthly Target") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val targetFloat = target.toFloatOrNull()
                    if (name.isNotBlank() && targetFloat != null) {
                        onConfirm(name, targetFloat)
                    }
                },
                // Disable the button if the input is invalid
                enabled = name.isNotBlank() && target.toFloatOrNull() != null
            ) {
                Text("Add")
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
fun AddCategoryDialogPreview() {
    WalletTheme {
        AddCategoryDialog(onDismissRequest = {}, onConfirm = { _, _ -> })
    }
}
