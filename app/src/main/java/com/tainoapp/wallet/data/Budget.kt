package com.tainoapp.wallet.data

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * Represents a single spending category.
 * @param name The name of the category (e.g., "Groceries").
 * @param target The budgeted amount for this category for the period.
 * @param currentAmount The amount spent or saved in this category so far.
 * @param colorValue The raw ULong value of the color, for serialization.
 */
@Serializable
data class BudgetCategory(
    val name: String,
    val target: Float,
    val currentAmount: Float,
    val colorValue: ULong
)

/**
 * Defines the different budgeting cycles a user can choose.
 */
enum class BudgetCycle {
    WEEKLY,
    BI_WEEKLY,
    MONTHLY
}
