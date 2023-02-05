package com.currency.exchange

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.currency.exchange.data.CurrencyExchangeRates
import com.currency.exchange.data.Rates
import com.currency.exchange.poll.ICurrencyExchangeRatesPoller
import com.currency.exchange.repository.pref.Balance
import com.currency.exchange.repository.pref.IDataManager
import com.currency.exchange.util.CurrencyExchangeHelper
import com.currency.exchange.util.NetworkConnectionHelper
import com.currency.exchange.viewmodel.CurrencyExchangeViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class CurrencyExchangeViewModelTest {

    private lateinit var currencyExchangeViewModel: CurrencyExchangeViewModel

    private lateinit var currencyExchangeHelper: CurrencyExchangeHelper

    private var networkConnectionHelperMockk: NetworkConnectionHelper = mockk()

    private var dataManagerMockk: IDataManager = mockk()

    private var currencyExchangeRatesPollerMockk: ICurrencyExchangeRatesPoller = mockk()

    private val context: Application = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        currencyExchangeHelper = CurrencyExchangeHelper()
        currencyExchangeViewModel = CurrencyExchangeViewModel(
            poller = currencyExchangeRatesPollerMockk,
            dataManager = dataManagerMockk,
            ioCoroutineContext = Dispatchers.IO,
            currencyExchangeHelper = currencyExchangeHelper,
            networkConnectionHelper = networkConnectionHelperMockk,
            context = context
        )
    }

    @Test
    fun testEmptySellAmountEventIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val receiveCurrencySymbol = context.getString(R.string.eur)
            val sellCurrencySymbol  = context.getString(R.string.usd)
            val expectedMessage = context.getString(R.string.empty_sell_amount)
            every { networkConnectionHelperMockk.isNetworkConnected() } returns true
            coEvery { dataManagerMockk.getCommissionCount() } returns 1
            currencyExchangeViewModel.setCurrencyExchangeRateState(null)
            currencyExchangeViewModel.submitClick("", receiveCurrencySymbol, sellCurrencySymbol )
            assertEquals(expectedMessage,
                (awaitItem() as CurrencyExchangeViewModel.CurrencyExchangeEvent.EmptySellAmount).message)
        }
    }

    @Test
    fun testIncorrectSelectedCurrencyIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val receiveCurrencySymbol = context.getString(R.string.eur)
            val sellCurrencySymbol  = context.getString(R.string.eur)
            val expectedMessage = context.getString(R.string.incorrect_selected_currency)
            every { networkConnectionHelperMockk.isNetworkConnected() } returns true
            coEvery { dataManagerMockk.getCommissionCount() } returns 1
            currencyExchangeViewModel.setCurrencyExchangeRateState(null)
            currencyExchangeViewModel.submitClick("100.0", receiveCurrencySymbol, sellCurrencySymbol )
            assertEquals(expectedMessage,
                (awaitItem() as CurrencyExchangeViewModel.CurrencyExchangeEvent.IncorrectSelectedCurrency).message)
        }
    }

    @Test
    fun testRateNotAvailableIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val receiveCurrencySymbol = context.getString(R.string.eur)
            val sellCurrencySymbol  = context.getString(R.string.usd)
            val expectedMessage = context.getString(R.string.exchange_rate_not_available)
            every { networkConnectionHelperMockk.isNetworkConnected() } returns true
            coEvery { dataManagerMockk.getCommissionCount() } returns 1
            currencyExchangeViewModel.setCurrencyExchangeRateState(null)
            currencyExchangeViewModel.submitClick("100.0", receiveCurrencySymbol, sellCurrencySymbol )
            assertEquals(expectedMessage,
                (awaitItem() as CurrencyExchangeViewModel.CurrencyExchangeEvent.RateNotAvailable).message)
        }
    }

    @Test
    fun testNetworkNotAvailableIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val receiveCurrencySymbol = context.getString(R.string.eur)
            val sellCurrencySymbol  = context.getString(R.string.usd)
            val expectedMessage = context.getString(R.string.network_not_available)
            every { networkConnectionHelperMockk.isNetworkConnected() } returns false
            coEvery { dataManagerMockk.getCommissionCount() } returns 1
            currencyExchangeViewModel.setCurrencyExchangeRateState(null)
            currencyExchangeViewModel.submitClick("100.0", receiveCurrencySymbol, sellCurrencySymbol )
            assertEquals(expectedMessage,
                (awaitItem() as CurrencyExchangeViewModel.CurrencyExchangeEvent.NetworkNotAvailable).message)
        }
    }

    @Test
    fun testNegativeBalanceIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val sellCurrencySymbol = context.getString(R.string.eur)
            val receiveCurrencySymbol = context.getString(R.string.usd)
            val expectedMessage = context.getString(R.string.balance_below_zero)
            val currencyExchangeRates = CurrencyExchangeRates(
                sellCurrencySymbol,
                LocalDateTime.now().toString(),
                Rates(20.0, 40.0, 60.0)
            )

            every { networkConnectionHelperMockk.isNetworkConnected() } returns true
            coEvery { dataManagerMockk.getCommissionCount() } returns 1
            coEvery { dataManagerMockk.getBalance() } returns Balance(100.0, 50.0, 100.0)

            currencyExchangeViewModel.setCurrencyExchangeRateState(currencyExchangeRates)
            currencyExchangeViewModel.submitClick("120.0", receiveCurrencySymbol, sellCurrencySymbol)
            assertEquals(expectedMessage,
                (awaitItem() as CurrencyExchangeViewModel.CurrencyExchangeEvent.NegativeBalance).message)
        }
    }

    @Test
    fun testConvertedAmountSuccessIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val sellCurrencySymbol = context.getString(R.string.eur)
            val receiveCurrencySymbol = context.getString(R.string.usd)
            val expectedReceiveAmountFormatted = "4800.0 $receiveCurrencySymbol"
            val expectedSellAmountFormatted = "120.0 $sellCurrencySymbol"
            val expectedConvertedAmount = "+4800.0"
            val expectedMessage = String.format(context.getString(R.string.currency_convert_success_commission_free),
                expectedSellAmountFormatted, expectedReceiveAmountFormatted)

            val currencyExchangeRates = CurrencyExchangeRates(
                sellCurrencySymbol,
                LocalDateTime.now().toString(),
                Rates(20.0, 40.0, 60.0)
            )

            every { networkConnectionHelperMockk.isNetworkConnected() } returns true
            coEvery { dataManagerMockk.getCommissionCount() } returns 1
            coEvery { dataManagerMockk.getBalance() } returns Balance(150.0, 50.0, 100.0)
            coEvery { dataManagerMockk.saveBalance(any()) } returns Unit
            coEvery { dataManagerMockk.saveCommissionCount(any()) } returns Unit

            currencyExchangeViewModel.setCurrencyExchangeRateState(currencyExchangeRates)
            currencyExchangeViewModel.submitClick("120.0", receiveCurrencySymbol, sellCurrencySymbol)
            val result = awaitItem()
            assertEquals(expectedMessage,
                (result as CurrencyExchangeViewModel.CurrencyExchangeEvent.ConvertedAmountSuccess).message)
            assertEquals(expectedConvertedAmount, result.convertedAmount)
        }
    }

    @Test
    fun testConvertedAmountWithCommissionSuccessIsTriggered() = runBlocking {
        currencyExchangeViewModel.currencyExchangeEvent.test {
            val sellCurrencySymbol = context.getString(R.string.eur)
            val receiveCurrencySymbol = context.getString(R.string.usd)
            val expectedReceiveAmountFormatted = "12000.0 $receiveCurrencySymbol"
            val expectedSellAmountFormatted = "300.0 $sellCurrencySymbol"
            val expectedCommissionAmountFormatted= "2.1 $sellCurrencySymbol"
            val expectedConvertedAmount = "+12000.0"
            val expectedMessage = String.format(context.getString(R.string.currency_convert_success),
                expectedSellAmountFormatted, expectedReceiveAmountFormatted, expectedCommissionAmountFormatted)

            val currencyExchangeRates = CurrencyExchangeRates(
                sellCurrencySymbol,
                LocalDateTime.now().toString(),
                Rates(20.0, 40.0, 60.0)
            )

            every { networkConnectionHelperMockk.isNetworkConnected() } returns true
            coEvery { dataManagerMockk.getCommissionCount() } returns 12
            coEvery { dataManagerMockk.getBalance() } returns Balance(5000.0, 50.0, 100.0)
            coEvery { dataManagerMockk.saveBalance(any()) } returns Unit
            coEvery { dataManagerMockk.saveCommissionCount(any()) } returns Unit

            currencyExchangeViewModel.setCurrencyExchangeRateState(currencyExchangeRates)
            currencyExchangeViewModel.submitClick("300.0", receiveCurrencySymbol, sellCurrencySymbol)
            val result = awaitItem()
            assertEquals(expectedMessage,
                (result as CurrencyExchangeViewModel.CurrencyExchangeEvent.ConvertedAmountSuccess).message)
            assertEquals(expectedConvertedAmount, result.convertedAmount)
        }
    }
}