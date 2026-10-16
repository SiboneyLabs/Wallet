package com.tainoapp.wallet.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.ui.theme.WalletTheme

@Composable
fun CategoriesCard(
    categories: List<BudgetCategory>,
    isRemoveModeEnabled: Boolean,
    onAddCategory: () -> Unit,
    onRemoveCategory: () -> Unit,
    onCategoryClick: (BudgetCategory) -> Unit,
    onCategoryRemoveClick: (BudgetCategory) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    IconButton(onClick = onAddCategory) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                    IconButton(onClick = onRemoveCategory) {
                        Icon(Icons.Default.Remove, contentDescription = "Remove Category")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Reduced spacer for tighter layout

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
fun CategoriesCardPreview() {
    WalletTheme {
        CategoriesCard(
            categories = listOf(
                BudgetCategory("Groceries", 400f, 150f, Color.Green.value),
                BudgetCategory("Gas", 150f, 75f, Color.Red.value)
            ),
            isRemoveModeEnabled = false,
            onAddCategory = {},
            onRemoveCategory = {},
            onCategoryClick = {},
            onCategoryRemoveClick = {}
        )
    }
}
