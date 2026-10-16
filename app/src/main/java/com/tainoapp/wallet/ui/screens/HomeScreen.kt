package com.tainoapp.wallet.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tainoapp.wallet.WalletApplication
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.ui.screens.home.AddCategoryDialog
import com.tainoapp.wallet.ui.screens.home.BalanceCard
import com.tainoapp.wallet.ui.screens.home.EditBalanceDialog
import com.tainoapp.wallet.ui.screens.home.ExpenseOverviewCard
import com.tainoapp.wallet.ui.screens.home.UpdateCategoryDialog
import com.tainoapp.wallet.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen() {
    val application = LocalContext.current.applicationContext as WalletApplication
    val repository = application.userPreferencesRepository

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(repository)
    )

    val balance by homeViewModel.balance.collectAsState()
    val categories by homeViewModel.categories.collectAsState()
    val budgetCycle by homeViewModel.budgetCycle.collectAsState()

    var showEditBalanceDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showUpdateCategoryDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<BudgetCategory?>(null) }
    var isRemoveModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BalanceCard(balance = balance, onClick = { showEditBalanceDialog = true })

        ExpenseOverviewCard(
            budgetCycle = budgetCycle,
            categories = categories,
            isRemoveModeEnabled = isRemoveModeEnabled,
            onAddCategory = { showAddCategoryDialog = true },
            onRemoveCategory = { isRemoveModeEnabled = !isRemoveModeEnabled },
            onCategoryClick = { category ->
                selectedCategory = category
                showUpdateCategoryDialog = true
            },
            onCategoryRemoveClick = { category ->
                homeViewModel.removeCategory(category)
            }
        )
    }

    if (showEditBalanceDialog) {
        EditBalanceDialog(
            currentBalance = balance,
            onDismissRequest = { showEditBalanceDialog = false },
            onConfirm = {
                homeViewModel.updateBalance(it)
                showEditBalanceDialog = false
            }
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            onConfirm = { name, target ->
                homeViewModel.addCategory(name, target)
                showAddCategoryDialog = false
            }
        )
    }

    selectedCategory?.let { category ->
        if (showUpdateCategoryDialog) {
            UpdateCategoryDialog(
                category = category,
                onDismissRequest = { showUpdateCategoryDialog = false },
                onConfirm = { amountToAdd, newTarget ->
                    homeViewModel.updateCategory(category, amountToAdd, newTarget)
                    showUpdateCategoryDialog = false
                },
                onClear = {
                    homeViewModel.clearCategory(category)
                    showUpdateCategoryDialog = false
                }
            )
        }
    }
}
