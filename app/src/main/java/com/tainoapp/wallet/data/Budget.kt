package com.tainoapp.wallet.data

import androidx.compose.ui.graphics.Color

/**
 * Represents the user's total balance.
 * @param totalAmount The total available money.
 */
data class Balance(val totalAmount: Float)

/**
 * Represents a single spending category.
 * @param name The name of the category (e.g., "Groceries").
 * @param target The budgeted amount for this category for the period.
 * @param currentAmount The amount spent or saved in this category so far.
 * @param color The color associated with the category for UI purposes.
 */
data class BudgetCategory(
    val name: String,
    val target: Float,
    val currentAmount: Float,
    val color: Color
)

/**
 * Defines the different budgeting cycles a user can choose.
 */
enum class BudgetCycle {
    WEEKLY,
    BI_WEEKLY,
    MONTHLY
}
