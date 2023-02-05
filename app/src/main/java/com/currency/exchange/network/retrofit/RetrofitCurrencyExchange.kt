package com.currency.exchange.network.retrofit

import com.currency.exchange.network.ICurrencyExchangeApi
import com.currency.exchange.network.ICurrencyExchangeService
import com.currency.exchange.network.RetrofitCurrencyExchangeApi

fun buildCurrencyExchangeApi(): ICurrencyExchangeApi {
   val service = retrofit.create(ICurrencyExchangeService::class.java)
   return RetrofitCurrencyExchangeApi(service)
}
