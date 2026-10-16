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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

data class HomeUiState(
    val balance: Float = 0f,
    val currentCategories: List<BudgetCategory> = emptyList(),
    val budgetCycle: BudgetCycle = BudgetCycle.WEEKLY,
    val currentPeriodKey: String = "",
    val isEditBalanceDialogShown: Boolean = false,
    val isAddCategoryDialogShown: Boolean = false,
    val isUpdateCategoryDialogShown: Boolean = false,
    val isRemoveModeEnabled: Boolean = false,
    val categoryToUpdate: BudgetCategory? = null
)

class HomeViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _budgetHistory = MutableStateFlow<Map<String, List<BudgetCategory>>>(emptyMap())
    private val _dialogAndModeState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = combine(
        userPreferencesRepository.balance,
        userPreferencesRepository.budgetCycle,
        _budgetHistory,
        _dialogAndModeState
    ) { balance, cycle, history, dialogState ->
        val currentPeriodKey = DateUtils.getPeriodKey(Date(), cycle)
        HomeUiState(
            balance = balance,
            budgetCycle = cycle,
            currentPeriodKey = currentPeriodKey,
            currentCategories = history[currentPeriodKey] ?: emptyList(),
            isEditBalanceDialogShown = dialogState.isEditBalanceDialogShown,
            isAddCategoryDialogShown = dialogState.isAddCategoryDialogShown,
            isUpdateCategoryDialogShown = dialogState.isUpdateCategoryDialogShown,
            isRemoveModeEnabled = dialogState.isRemoveModeEnabled,
            categoryToUpdate = dialogState.categoryToUpdate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            userPreferencesRepository.budgetCycle.collect { cycle ->
                var history = userPreferencesRepository.budgetHistory.first()
                val currentPeriodKey = DateUtils.getPeriodKey(Date(), cycle)

                if (!history.containsKey(currentPeriodKey)) {
                    val lastPeriodKey = history.keys.maxOrNull()
                    val categoriesToCopy = if (lastPeriodKey != null) history[lastPeriodKey]!! else getDefaultCategories()
                    val newPeriodCategories = categoriesToCopy.map { it.copy(currentAmount = 0f) }
                    history = history + (currentPeriodKey to newPeriodCategories)
                    userPreferencesRepository.saveBudgetHistory(history)
                }
                _budgetHistory.value = history
            }
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

    private fun updateAndSaveCategories(newCategories: List<BudgetCategory>) {
        val currentPeriodKey = DateUtils.getPeriodKey(Date(), uiState.value.budgetCycle)
        val newHistory = _budgetHistory.value + (currentPeriodKey to newCategories)
        _budgetHistory.value = newHistory
        viewModelScope.launch { userPreferencesRepository.saveBudgetHistory(newHistory) }
    }

    fun onOpenEditBalanceDialog() = _dialogAndModeState.update { it.copy(isEditBalanceDialogShown = true) }
    fun onOpenAddCategoryDialog() = _dialogAndModeState.update { it.copy(isAddCategoryDialogShown = true) }
    fun onOpenUpdateCategoryDialog(category: BudgetCategory) = _dialogAndModeState.update { it.copy(isUpdateCategoryDialogShown = true, categoryToUpdate = category) }
    fun onToggleRemoveMode() = _dialogAndModeState.update { it.copy(isRemoveModeEnabled = !it.isRemoveModeEnabled) }
    fun onCloseDialogs() = _dialogAndModeState.update { it.copy(isEditBalanceDialogShown = false, isAddCategoryDialogShown = false, isUpdateCategoryDialogShown = false, categoryToUpdate = null) }

    fun onAddCategory(name: String, target: Float) {
        val newCategory = BudgetCategory(name, target, 0f, Color(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)).value)
        updateAndSaveCategories(uiState.value.currentCategories + newCategory)
        onCloseDialogs()
    }

    fun onRemoveCategory(category: BudgetCategory) {
        updateAndSaveCategories(uiState.value.currentCategories.filter { it.name != category.name })
        _dialogAndModeState.update { it.copy(isRemoveModeEnabled = false) } // Exit remove mode
    }

    fun onUpdateCategory(category: BudgetCategory, amountToAdd: Float, newTarget: Float) {
        val newBalance = uiState.value.balance - amountToAdd
        val updatedCategory = category.copy(currentAmount = category.currentAmount + amountToAdd, target = newTarget)
        updateAndSaveCategories(uiState.value.currentCategories.map { if (it.name == category.name) updatedCategory else it })
        onUpdateBalance(newBalance)
        onCloseDialogs()
    }

    fun onClearCategory(category: BudgetCategory) {
        val newBalance = uiState.value.balance + category.currentAmount
        val clearedCategory = category.copy(currentAmount = 0f, target = 0f)
        updateAndSaveCategories(uiState.value.currentCategories.map { if (it.name == category.name) clearedCategory else it })
        onUpdateBalance(newBalance)
        onCloseDialogs()
    }

    fun onUpdateBalance(newBalance: Float) {
        _dialogAndModeState.update { it.copy(isEditBalanceDialogShown = false) }
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
