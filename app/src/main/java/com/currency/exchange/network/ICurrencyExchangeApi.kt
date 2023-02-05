package com.currency.exchange.network

import com.currency.exchange.data.CurrencyExchangeRates

interface ICurrencyExchangeApi {
    suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates?
}