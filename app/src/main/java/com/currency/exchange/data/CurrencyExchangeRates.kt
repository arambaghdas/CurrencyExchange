package com.currency.exchange.data

import com.google.gson.annotations.SerializedName

data class CurrencyExchangeRates(
    val base: String,
    val date: String,
    val rates: Rates
)

data class Rates (
    @SerializedName("EUR")
    val eur: Double,
    @SerializedName("USD")
    val usd: Double,
    @SerializedName("BGN")
    val bgn: Double
)