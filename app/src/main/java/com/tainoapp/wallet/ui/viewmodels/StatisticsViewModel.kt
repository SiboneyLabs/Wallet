package com.tainoapp.wallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class StatisticsUiState(
    val budgetHistory: Map<String, List<BudgetCategory>> = emptyMap()
)

class StatisticsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val history = userPreferencesRepository.budgetHistory.first()
            // Sort the history so the most recent period is first.
            val sortedHistory = history.toSortedMap(compareByDescending { it })
            _uiState.value = StatisticsUiState(budgetHistory = sortedHistory)
        }
    }

    companion object {
        fun provideFactory(userPreferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StatisticsViewModel(userPreferencesRepository) as T
            }
        }
    }
}
