package com.tainoapp.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tainoapp.wallet.WalletApplication
import com.tainoapp.wallet.ui.screens.home.AddCategoryDialog
import com.tainoapp.wallet.ui.screens.home.BalanceCard
import com.tainoapp.wallet.ui.screens.home.CategoriesCard
import com.tainoapp.wallet.ui.screens.home.CurrentPeriodCard
import com.tainoapp.wallet.ui.screens.home.EditBalanceDialog
import com.tainoapp.wallet.ui.screens.home.UpdateCategoryDialog
import com.tainoapp.wallet.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen() {
    val application = LocalContext.current.applicationContext as WalletApplication
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(application.userPreferencesRepository)
    )

    val uiState by homeViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BalanceCard(balance = uiState.balance, onClick = homeViewModel::onOpenEditBalanceDialog)
        
        CurrentPeriodCard(currentPeriodKey = uiState.currentPeriodKey)

        CategoriesCard(
            categories = uiState.currentCategories,
            isRemoveModeEnabled = uiState.isRemoveModeEnabled,
            onAddCategory = homeViewModel::onOpenAddCategoryDialog,
            onRemoveCategory = homeViewModel::onToggleRemoveMode,
            onCategoryClick = { homeViewModel.onOpenUpdateCategoryDialog(it) },
            onCategoryRemoveClick = { homeViewModel.onRemoveCategory(it) }
        )
    }

    if (uiState.isEditBalanceDialogShown) {
        EditBalanceDialog(
            currentBalance = uiState.balance,
            onDismissRequest = homeViewModel::onCloseDialogs,
            onConfirm = { homeViewModel.onUpdateBalance(it) }
        )
    }

    if (uiState.isAddCategoryDialogShown) {
        AddCategoryDialog(
            onDismissRequest = homeViewModel::onCloseDialogs,
            onConfirm = { name, target -> homeViewModel.onAddCategory(name, target) }
        )
    }

    uiState.categoryToUpdate?.let {
        if (uiState.isUpdateCategoryDialogShown) {
            UpdateCategoryDialog(
                category = it,
                onDismissRequest = homeViewModel::onCloseDialogs,
                onConfirm = { amountToAdd, newTarget -> homeViewModel.onUpdateCategory(it, amountToAdd, newTarget) },
                onClear = { homeViewModel.onClearCategory(it) }
            )
        }
    }
}
