package com.currency.exchange.repository.pref

import kotlinx.coroutines.flow.Flow

interface IDataManager {
    suspend fun saveBalance(balance: Balance)
    suspend fun getBalanceFlow(): Flow<Balance>
    suspend fun getBalance(): Balance
    suspend fun getCommissionCount(): Int
    suspend fun saveCommissionCount(commissionCount: Int)
}