package com.tainoapp.wallet.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tainoapp.wallet.data.BudgetCycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val TOTAL_BALANCE = floatPreferencesKey("total_balance")
        val BUDGET_CYCLE = stringPreferencesKey("budget_cycle")
        val LAST_ROLLOVER_TIMESTAMP = longPreferencesKey("last_rollover_timestamp")
        const val TAG = "UserPrefsRepository"
    }

    val balance: Flow<Float> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[TOTAL_BALANCE] ?: 0f
        }

    val budgetCycle: Flow<BudgetCycle> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val cycleName = preferences[BUDGET_CYCLE] ?: BudgetCycle.WEEKLY.name
            BudgetCycle.valueOf(cycleName)
        }

    val lastRolloverTimestamp: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[LAST_ROLLOVER_TIMESTAMP] ?: 0L
        }

    suspend fun updateBalance(newBalance: Float) {
        dataStore.edit {
            it[TOTAL_BALANCE] = newBalance
        }
    }

    suspend fun updateBudgetCycle(budgetCycle: BudgetCycle) {
        dataStore.edit {
            it[BUDGET_CYCLE] = budgetCycle.name
        }
    }

    suspend fun updateLastRolloverTimestamp(timestamp: Long) {
        dataStore.edit {
            it[LAST_ROLLOVER_TIMESTAMP] = timestamp
        }
    }
}
