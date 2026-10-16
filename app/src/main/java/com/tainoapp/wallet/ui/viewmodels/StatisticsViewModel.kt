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

data class StatisticsUiState(
    val relevantHistory: Map<String, List<BudgetCategory>> = emptyMap()
)

class StatisticsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = combine(
        userPreferencesRepository.budgetHistory,
        userPreferencesRepository.budgetCycle
    ) { history, cycle ->
        val keyPrefix = when (cycle) {
            BudgetCycle.WEEKLY -> "W-"
            BudgetCycle.BI_WEEKLY -> "B-"
            BudgetCycle.MONTHLY -> "M-"
        }
        
        val filteredHistory = history.filter { it.key.startsWith(keyPrefix) }
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
