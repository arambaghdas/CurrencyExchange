package com.currency.exchange.network

import com.currency.exchange.data.CurrencyExchangeRates
import retrofit2.Response
import retrofit2.http.GET

interface ICurrencyExchangeService {
    @GET("tasks/api/currency-exchange-rates")
    suspend fun getCurrencyExchangeRates(): Response<CurrencyExchangeRates>
}
