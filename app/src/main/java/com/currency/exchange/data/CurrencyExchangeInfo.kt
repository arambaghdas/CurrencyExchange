package com.currency.exchange.data

data class CurrencyExchangeInfo(
    val receiveAmount: Double,
    val sellAmount: Double,
    val commissionAmount: Double,
    val exchangeRate: Double?,
    val receiveCurrencySymbol: String,
    val sellCurrencySymbol: String
)
