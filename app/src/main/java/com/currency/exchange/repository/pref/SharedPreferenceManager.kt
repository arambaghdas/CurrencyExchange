package com.currency.exchange.repository.pref

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SharedPreferenceManager(
    private val context: Context
): IDataManager {
    private val defaultBalance = Balance(EURO_DEFAULT_AMOUNT, USD_DEFAULT_AMOUNT, BGN_DEFAULT_AMOUNT)
    private val balanceKey = stringPreferencesKey("BALANCE")
    private val commissionCountKey = intPreferencesKey("COMMISSION_COUNT")

    private val Context.dataStore by preferencesDataStore(
        name = "shared_pref_balance"
    )

    override suspend fun saveBalance(balance: Balance) {
        context.dataStore.edit { preferences ->
            preferences[balanceKey] = Gson().toJson(balance)
        }
    }

    override suspend fun getBalanceFlow(): Flow<Balance> =
        context.dataStore.data.map { preferences ->
                getBalanceFromPref(preferences)
            }

    override suspend fun getBalance(): Balance {
        return getBalanceFromPref(context.dataStore.data.first())
    }

    override suspend fun getCommissionCount(): Int {
        return context.dataStore.data.first()[commissionCountKey] ?: 0
    }

    override suspend fun saveCommissionCount(commissionCount: Int) {
        context.dataStore.edit { preferences ->
            preferences[commissionCountKey] = commissionCount
        }
    }

    private fun getBalanceFromPref(preferences: Preferences): Balance {
        return Gson().fromJson(preferences[balanceKey], Balance::class.java)  ?: defaultBalance
    }

    companion object {
        const val EURO_DEFAULT_AMOUNT = 1000.0
        const val USD_DEFAULT_AMOUNT = 0.0
        const val BGN_DEFAULT_AMOUNT = 0.0
    }
}