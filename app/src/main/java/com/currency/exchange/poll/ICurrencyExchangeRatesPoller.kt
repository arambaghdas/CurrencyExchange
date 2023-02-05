package com.currency.exchange.poll

import com.currency.exchange.data.CurrencyExchangeRates
import kotlinx.coroutines.flow.Flow

interface ICurrencyExchangeRatesPoller {
    suspend fun currencyExchangeRatesPoll(): Flow<CurrencyExchangeRates?>
}