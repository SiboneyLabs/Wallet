package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.data.BudgetCycle
import com.tainoapp.wallet.ui.theme.WalletTheme

/**
 * A card that displays an overview of the user's expenses for a given budget cycle.
 * @param budgetCycle The current budget cycle, used for the card's title.
 * @param categories The list of categories to display.
 * @param isRemoveModeEnabled Whether the UI is in remove mode.
 * @param onAddCategory The action to perform when the add button is tapped.
 * @param onRemoveCategory The action to perform when the remove button is tapped.
 * @param onCategoryClick The action to perform when a category is tapped.
 * @param onCategoryRemoveClick The action for when a category's remove icon is tapped.
 */
@Composable
fun ExpenseOverviewCard(
    budgetCycle: BudgetCycle,
    categories: List<BudgetCategory>,
    isRemoveModeEnabled: Boolean,
    onAddCategory: () -> Unit,
    onRemoveCategory: () -> Unit,
    onCategoryClick: (BudgetCategory) -> Unit,
    onCategoryRemoveClick: (BudgetCategory) -> Unit
) {
    val title = when (budgetCycle) {
        BudgetCycle.WEEKLY -> "Weekly Overview"
        BudgetCycle.BI_WEEKLY -> "Bi-Weekly Overview"
        BudgetCycle.MONTHLY -> "Monthly Overview"
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = onAddCategory) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                    IconButton(onClick = onRemoveCategory) {
                        Icon(Icons.Default.Remove, contentDescription = "Remove Category")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (categories.isEmpty()) {
                Text("No categories yet. Tap the '+' to add one!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        CategoryItem(
                            category = category,
                            isRemoveMode = isRemoveModeEnabled,
                            onClick = { onCategoryClick(category) },
                            onRemoveClick = { onCategoryRemoveClick(category) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseOverviewCardPreview() {
    WalletTheme {
        ExpenseOverviewCard(
            budgetCycle = BudgetCycle.WEEKLY,
            categories = listOf(
                BudgetCategory("Groceries", 400f, 150f, Color.Green),
                BudgetCategory("Gas", 150f, 75f, Color.Red)
            ),
            isRemoveModeEnabled = false,
            onAddCategory = {},
            onRemoveCategory = {},
            onCategoryClick = {},
            onCategoryRemoveClick = {}
        )
    }
}
