package com.currency.exchange.poll

import com.currency.exchange.data.CurrencyExchangeRates
import com.currency.exchange.repository.ICurrencyExchangeRepository
import com.currency.exchange.util.NetworkConnectionHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class CurrencyExchangeRatesPoller(
    private val dispatcher: CoroutineDispatcher,
    private val repository: ICurrencyExchangeRepository,
    private val networkConnectionHelper: NetworkConnectionHelper,
): ICurrencyExchangeRatesPoller {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun currencyExchangeRatesPoll(): Flow<CurrencyExchangeRates?> {
        return channelFlow {
            while (!isClosedForSend && networkConnectionHelper.isNetworkConnected()) {
                delay(DELAY_IN_MLS)
                send(repository.getCurrencyExchangeRates())
            }
        }.flowOn(dispatcher)
    }

    companion object {
        const val DELAY_IN_MLS = 5000L
    }
}