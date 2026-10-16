package com.tainoapp.wallet.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.data.BudgetCycle
import com.tainoapp.wallet.repository.UserPreferencesRepository
import com.tainoapp.wallet.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val balance: StateFlow<Float> = userPreferencesRepository.balance
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0f
        )

    val budgetCycle: StateFlow<BudgetCycle> = userPreferencesRepository.budgetCycle
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetCycle.WEEKLY
        )

    private val _categories = MutableStateFlow<List<BudgetCategory>>(emptyList())
    val categories: StateFlow<List<BudgetCategory>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            // Check for budget rollover
            val lastRollover = userPreferencesRepository.lastRolloverTimestamp.first() // .first() is ok here, as it's a one-time setup
            val currentCycle = budgetCycle.value

            if (DateUtils.isNewPeriod(lastRollover, currentCycle)) {
                resetAllCategoryAmounts()
                userPreferencesRepository.updateLastRolloverTimestamp(System.currentTimeMillis())
            }
        }

        // Initialize with default categories (or load them from storage in a real app)
        if (_categories.value.isEmpty()) {
            _categories.value = listOf(
                BudgetCategory("Mortgage", 0f, 0f, Color(0xFFF44336)),
                BudgetCategory("Groceries", 0f, 0f, Color(0xFF4CAF50)),
                BudgetCategory("Gas", 0f, 0f, Color(0xFFFF9800)),
                BudgetCategory("Other", 0f, 0f, Color(0xFF2196F3)),
                BudgetCategory("Savings", 0f, 0f, Color(0xFF9C27B0))
            )
        }
    }

    private fun resetAllCategoryAmounts() {
        _categories.value = _categories.value.map { it.copy(currentAmount = 0f) }
    }

    fun addCategory(name: String, target: Float) {
        val newCategory = BudgetCategory(
            name = name,
            target = target,
            currentAmount = 0f,
            color = Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        )
        _categories.value = _categories.value + newCategory
    }

    fun removeCategory(category: BudgetCategory) {
        _categories.value = _categories.value.filter { it.name != category.name }
    }

    fun updateCategory(category: BudgetCategory, amountToAdd: Float, newTarget: Float) {
        val currentBalance = balance.value
        updateBalance(currentBalance - amountToAdd)

        val updatedCategory = category.copy(
            currentAmount = category.currentAmount + amountToAdd,
            target = newTarget
        )
        _categories.value = _categories.value.map {
            if (it.name == category.name) updatedCategory else it
        }
    }

    fun clearCategory(category: BudgetCategory) {
        val currentBalance = balance.value
        updateBalance(currentBalance + category.currentAmount)

        val clearedCategory = category.copy(currentAmount = 0f, target = 0f)
        _categories.value = _categories.value.map {
            if (it.name == category.name) clearedCategory else it
        }
    }

    fun updateBalance(newBalance: Float) {
        viewModelScope.launch {
            userPreferencesRepository.updateBalance(newBalance)
        }
    }

    companion object {
        fun provideFactory(
            userPreferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(userPreferencesRepository) as T
            }
        }
    }
}
