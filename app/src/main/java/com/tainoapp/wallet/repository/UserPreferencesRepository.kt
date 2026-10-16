package com.tainoapp.wallet.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tainoapp.wallet.data.BudgetCategory
import com.tainoapp.wallet.data.BudgetCycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val TOTAL_BALANCE = floatPreferencesKey("total_balance")
        val BUDGET_CYCLE = stringPreferencesKey("budget_cycle")
        val LAST_ROLLOVER_TIMESTAMP = longPreferencesKey("last_rollover_timestamp")
        val CATEGORIES_JSON = stringPreferencesKey("categories_json")
        const val TAG = "UserPrefsRepository"
    }

    val balance: Flow<Float> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading balance.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { it[TOTAL_BALANCE] ?: 0f }

    val budgetCycle: Flow<BudgetCycle> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading budget cycle.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { BudgetCycle.valueOf(it[BUDGET_CYCLE] ?: BudgetCycle.WEEKLY.name) }

    val lastRolloverTimestamp: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading rollover timestamp.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { it[LAST_ROLLOVER_TIMESTAMP] ?: 0L }

    val categories: Flow<List<BudgetCategory>> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading categories.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val jsonString = preferences[CATEGORIES_JSON]
            if (jsonString.isNullOrEmpty()) {
                emptyList()
            } else {
                Json.decodeFromString<List<BudgetCategory>>(jsonString)
            }
        }

    suspend fun updateBalance(newBalance: Float) {
        dataStore.edit { it[TOTAL_BALANCE] = newBalance }
    }

    suspend fun updateBudgetCycle(budgetCycle: BudgetCycle) {
        dataStore.edit { it[BUDGET_CYCLE] = budgetCycle.name }
    }

    suspend fun updateLastRolloverTimestamp(timestamp: Long) {
        dataStore.edit { it[LAST_ROLLOVER_TIMESTAMP] = timestamp }
    }

    suspend fun saveCategories(categories: List<BudgetCategory>) {
        val jsonString = Json.encodeToString(categories)
        dataStore.edit { it[CATEGORIES_JSON] = jsonString }
    }
}
