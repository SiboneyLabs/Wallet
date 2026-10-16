package com.tainoapp.wallet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tainoapp.wallet.data.BudgetCycle
import com.tainoapp.wallet.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val budgetCycle: StateFlow<BudgetCycle> = userPreferencesRepository.budgetCycle
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetCycle.WEEKLY // Default value
        )

    fun updateBudgetCycle(newCycle: BudgetCycle) {
        viewModelScope.launch {
            userPreferencesRepository.updateBudgetCycle(newCycle)
        }
    }

    companion object {
        fun provideFactory(
            userPreferencesRepository: UserPreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(userPreferencesRepository) as T
            }
        }
    }
}
