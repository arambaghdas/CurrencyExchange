package com.currency.exchange.network.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit: Retrofit by lazy {
    val url = "https://developers.paysera.com/"
    val client = OkHttpClient.Builder().build()
    Retrofit.Builder()
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(url)
        .build()
}