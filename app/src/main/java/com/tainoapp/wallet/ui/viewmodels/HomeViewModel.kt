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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

data class HomeUiState(
    val balance: Float = 0f,
    val currentCategories: List<BudgetCategory> = emptyList(),
    val budgetCycle: BudgetCycle = BudgetCycle.WEEKLY,
    val isEditBalanceDialogShown: Boolean = false,
    val isAddCategoryDialogShown: Boolean = false,
    val isUpdateCategoryDialogShown: Boolean = false,
    val isRemoveModeEnabled: Boolean = false,
    val categoryToUpdate: BudgetCategory? = null
)

class HomeViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val initialBalance = userPreferencesRepository.balance.first()
            val initialCycle = userPreferencesRepository.budgetCycle.first()
            var history = userPreferencesRepository.budgetHistory.first()

            val currentPeriodKey = DateUtils.getPeriodKey(Date(), initialCycle)

            // If the current period doesn't exist, it's a new period. Time to roll over.
            if (!history.containsKey(currentPeriodKey)) {
                val lastPeriodKey = history.keys.maxOrNull() // Find the most recent period
                val categoriesToCopy = if (lastPeriodKey != null) {
                    history[lastPeriodKey]!!
                } else {
                    // If no history exists, it's the first ever run.
                    getDefaultCategories()
                }
                // Reset amounts for the new period.
                val newPeriodCategories = categoriesToCopy.map { it.copy(currentAmount = 0f) }
                history = history + (currentPeriodKey to newPeriodCategories)
                userPreferencesRepository.saveBudgetHistory(history)
            }

            _uiState.value = HomeUiState(
                balance = initialBalance,
                budgetCycle = initialCycle,
                currentCategories = history[currentPeriodKey] ?: emptyList()
            )
        }
    }

    private fun getDefaultCategories(): List<BudgetCategory> {
        return listOf(
            BudgetCategory("Mortgage", 0f, 0f, Color(0xFFF44336).value),
            BudgetCategory("Groceries", 0f, 0f, Color(0xFF4CAF50).value),
            BudgetCategory("Gas", 0f, 0f, Color(0xFFFF9800).value),
            BudgetCategory("Other", 0f, 0f, Color(0xFF2196F3).value),
            BudgetCategory("Savings", 0f, 0f, Color(0xFF9C27B0).value)
        )
    }

    // --- Event Handlers --- //

    private fun updateAndSaveCategories(newCategories: List<BudgetCategory>) {
        val currentPeriodKey = DateUtils.getPeriodKey(Date(), _uiState.value.budgetCycle)
        viewModelScope.launch {
            val currentHistory = userPreferencesRepository.budgetHistory.first()
            val newHistory = currentHistory + (currentPeriodKey to newCategories)
            userPreferencesRepository.saveBudgetHistory(newHistory)
            _uiState.update { it.copy(currentCategories = newCategories) }
        }
    }

    fun onOpenEditBalanceDialog() = _uiState.update { it.copy(isEditBalanceDialogShown = true) }
    fun onOpenAddCategoryDialog() = _uiState.update { it.copy(isAddCategoryDialogShown = true) }
    fun onOpenUpdateCategoryDialog(category: BudgetCategory) = _uiState.update { it.copy(isUpdateCategoryDialogShown = true, categoryToUpdate = category) }
    fun onToggleRemoveMode() = _uiState.update { it.copy(isRemoveModeEnabled = !it.isRemoveModeEnabled) }
    fun onCloseDialogs() = _uiState.update { it.copy(isEditBalanceDialogShown = false, isAddCategoryDialogShown = false, isUpdateCategoryDialogShown = false, categoryToUpdate = null) }

    fun onAddCategory(name: String, target: Float) {
        val newCategory = BudgetCategory(name, target, 0f, Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)).value)
        val newCategories = _uiState.value.currentCategories + newCategory
        _uiState.update { it.copy(isAddCategoryDialogShown = false) } 
        updateAndSaveCategories(newCategories)
    }

    fun onRemoveCategory(category: BudgetCategory) {
        val newCategories = _uiState.value.currentCategories.filter { c -> c.name != category.name }
        updateAndSaveCategories(newCategories)
    }

    fun onUpdateCategory(category: BudgetCategory, amountToAdd: Float, newTarget: Float) {
        val newBalance = _uiState.value.balance - amountToAdd
        val updatedCategory = category.copy(currentAmount = category.currentAmount + amountToAdd, target = newTarget)
        val newCategories = _uiState.value.currentCategories.map { c -> if (c.name == category.name) updatedCategory else c }

        _uiState.update { it.copy(balance = newBalance, isUpdateCategoryDialogShown = false, categoryToUpdate = null) }
        updateAndSaveCategories(newCategories)

        viewModelScope.launch { userPreferencesRepository.updateBalance(newBalance) }
    }

    fun onClearCategory(category: BudgetCategory) {
        val newBalance = _uiState.value.balance + category.currentAmount
        val clearedCategory = category.copy(currentAmount = 0f, target = 0f)
        val newCategories = _uiState.value.currentCategories.map { c -> if (c.name == category.name) clearedCategory else c }

        _uiState.update { it.copy(balance = newBalance, isUpdateCategoryDialogShown = false, categoryToUpdate = null) }
        updateAndSaveCategories(newCategories)

        viewModelScope.launch { userPreferencesRepository.updateBalance(newBalance) }
    }

    fun onUpdateBalance(newBalance: Float) {
        _uiState.update { it.copy(balance = newBalance, isEditBalanceDialogShown = false) }
        viewModelScope.launch { userPreferencesRepository.updateBalance(newBalance) }
    }

    companion object {
        fun provideFactory(userPreferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(userPreferencesRepository) as T
            }
        }
    }
}
