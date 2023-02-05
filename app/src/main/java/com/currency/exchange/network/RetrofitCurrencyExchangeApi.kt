package com.currency.exchange.network

import com.currency.exchange.data.CurrencyExchangeRates

class RetrofitCurrencyExchangeApi(
    private val service: ICurrencyExchangeService
): ICurrencyExchangeApi {
    override suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates? {
        val response = service.getCurrencyExchangeRates()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}