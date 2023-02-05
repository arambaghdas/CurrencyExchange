package com.currency.exchange.di

import com.currency.exchange.repository.CurrencyExchangeCommonRepository
import com.currency.exchange.repository.ICurrencyExchangeRepository
import com.currency.exchange.network.retrofit.buildCurrencyExchangeApi
import com.currency.exchange.poll.CurrencyExchangeRatesPoller
import com.currency.exchange.poll.ICurrencyExchangeRatesPoller
import com.currency.exchange.repository.pref.IDataManager
import com.currency.exchange.repository.pref.SharedPreferenceManager
import com.currency.exchange.util.CurrencyExchangeHelper
import com.currency.exchange.util.NetworkConnectionHelper
import com.currency.exchange.viewmodel.CurrencyExchangeViewModel
import kotlinx.coroutines.Dispatchers.IO
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ICurrencyExchangeRepository> {
        CurrencyExchangeCommonRepository(
            buildCurrencyExchangeApi()
        )
    }

    single<ICurrencyExchangeRatesPoller> {
        CurrencyExchangeRatesPoller(
            dispatcher = IO,
            repository = get(),
            networkConnectionHelper = get()
        )
    }

    single<IDataManager> {
        SharedPreferenceManager(androidApplication())
    }

    single {
        CurrencyExchangeHelper()
    }

    single {
        NetworkConnectionHelper(androidApplication())
    }

    viewModel {
        CurrencyExchangeViewModel(
            poller = get(),
            dataManager = get(),
            ioCoroutineContext = IO,
            currencyExchangeHelper = get(),
            networkConnectionHelper = get(),
            context = androidApplication()
        )
    }
}