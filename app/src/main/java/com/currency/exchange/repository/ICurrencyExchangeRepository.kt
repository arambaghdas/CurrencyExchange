package com.currency.exchange.repository

import com.currency.exchange.data.CurrencyExchangeRates

interface ICurrencyExchangeRepository {
    suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates?
}