package com.currency.exchange.repository

import com.currency.exchange.data.CurrencyExchangeRates
import com.currency.exchange.network.ICurrencyExchangeApi

class CurrencyExchangeCommonRepository(
    private val api: ICurrencyExchangeApi
) : ICurrencyExchangeRepository {
    override suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates? {
        return api.getCurrencyExchangeRates()
    }
}