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
import kotlin.random.Random

data class HomeUiState(
    val balance: Float = 0f,
    val categories: List<BudgetCategory> = emptyList(),
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
            val lastRollover = userPreferencesRepository.lastRolloverTimestamp.first()
            var initialCategories = userPreferencesRepository.categories.first()

            if (initialCategories.isEmpty()) {
                initialCategories = getDefaultCategories()
                userPreferencesRepository.saveCategories(initialCategories)
            }

            if (DateUtils.isNewPeriod(lastRollover, initialCycle)) {
                initialCategories = initialCategories.map { it.copy(currentAmount = 0f) }
                userPreferencesRepository.updateLastRolloverTimestamp(System.currentTimeMillis())
                userPreferencesRepository.saveCategories(initialCategories)
            }

            _uiState.value = HomeUiState(balance = initialBalance, budgetCycle = initialCycle, categories = initialCategories)
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

    fun onOpenEditBalanceDialog() = _uiState.update { it.copy(isEditBalanceDialogShown = true) }
    fun onOpenAddCategoryDialog() = _uiState.update { it.copy(isAddCategoryDialogShown = true) }
    fun onOpenUpdateCategoryDialog(category: BudgetCategory) = _uiState.update { it.copy(isUpdateCategoryDialogShown = true, categoryToUpdate = category) }
    fun onToggleRemoveMode() = _uiState.update { it.copy(isRemoveModeEnabled = !it.isRemoveModeEnabled) }
    fun onCloseDialogs() = _uiState.update { it.copy(isEditBalanceDialogShown = false, isAddCategoryDialogShown = false, isUpdateCategoryDialogShown = false, categoryToUpdate = null) }

    fun onAddCategory(name: String, target: Float) {
        val newCategory = BudgetCategory(name, target, 0f, Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)).value)
        val newCategories = _uiState.value.categories + newCategory
        _uiState.update {
            it.copy(categories = newCategories, isAddCategoryDialogShown = false)
        }
        viewModelScope.launch { userPreferencesRepository.saveCategories(newCategories) }
    }

    fun onRemoveCategory(category: BudgetCategory) {
        val newCategories = _uiState.value.categories.filter { c -> c.name != category.name }
        _uiState.update {
            it.copy(categories = newCategories)
        }
        viewModelScope.launch { userPreferencesRepository.saveCategories(newCategories) }
    }

    fun onUpdateCategory(category: BudgetCategory, amountToAdd: Float, newTarget: Float) {
        val newBalance = _uiState.value.balance - amountToAdd
        val updatedCategory = category.copy(currentAmount = category.currentAmount + amountToAdd, target = newTarget)
        val newCategories = _uiState.value.categories.map { c -> if (c.name == category.name) updatedCategory else c }

        _uiState.update {
            it.copy(
                balance = newBalance,
                categories = newCategories,
                isUpdateCategoryDialogShown = false,
                categoryToUpdate = null
            )
        }
        viewModelScope.launch { 
            userPreferencesRepository.updateBalance(newBalance)
            userPreferencesRepository.saveCategories(newCategories)
        }
    }

    fun onClearCategory(category: BudgetCategory) {
        val newBalance = _uiState.value.balance + category.currentAmount
        val clearedCategory = category.copy(currentAmount = 0f, target = 0f)
        val newCategories = _uiState.value.categories.map { c -> if (c.name == category.name) clearedCategory else c }

        _uiState.update {
            it.copy(
                balance = newBalance,
                categories = newCategories,
                isUpdateCategoryDialogShown = false,
                categoryToUpdate = null
            )
        }
        viewModelScope.launch { 
            userPreferencesRepository.updateBalance(newBalance)
            userPreferencesRepository.saveCategories(newCategories) 
        }
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
