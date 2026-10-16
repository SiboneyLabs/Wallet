package com.tainoapp.wallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.data.BudgetCycle
import com.tainoapp.wallet.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


// Add totalAddedFunds to the UI state
data class PeriodHistory(
    val categories: List<BudgetCategory>,
    val totalAddedFunds: Float
)

data class StatisticsUiState(
    val relevantHistory: Map<String, PeriodHistory> = emptyMap()
)

class StatisticsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = combine(
        userPreferencesRepository.budgetHistory,
        userPreferencesRepository.fundsAddedHistory,
        userPreferencesRepository.budgetCycle
    ) { budgetHistory, fundsHistory, cycle ->
        val keyPrefix = when (cycle) {
            BudgetCycle.WEEKLY -> "W-"
            BudgetCycle.BI_WEEKLY -> "B-"
            BudgetCycle.MONTHLY -> "M-"
        }

        val filteredHistory = budgetHistory
            .filter { it.key.startsWith(keyPrefix) }
            .mapValues { (key, categories) ->
                PeriodHistory(
                    categories = categories,
                    totalAddedFunds = fundsHistory[key] ?: 0f
                )
            }

        val sortedHistory = filteredHistory.toSortedMap(compareByDescending { it })

        StatisticsUiState(relevantHistory = sortedHistory)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    companion object {
        fun provideFactory(userPreferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatisticsViewModel(userPreferencesRepository) as T
            }
        }
    }
}
