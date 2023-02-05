package com.currency.exchange.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currency.exchange.R
import com.currency.exchange.poll.ICurrencyExchangeRatesPoller
import com.currency.exchange.repository.pref.IDataManager
import com.currency.exchange.data.Currency.EUR
import com.currency.exchange.data.Currency.USD
import com.currency.exchange.data.Currency.BGN
import com.currency.exchange.data.CurrencyExchangeInfo
import com.currency.exchange.data.CurrencyExchangeRates
import com.currency.exchange.repository.pref.Balance
import com.currency.exchange.util.CurrencyExchangeHelper
import com.currency.exchange.util.NetworkConnectionHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CurrencyExchangeViewModel(
    private val poller: ICurrencyExchangeRatesPoller,
    private val dataManager: IDataManager,
    private val ioCoroutineContext: CoroutineContext,
    private val context: Application,
    private val currencyExchangeHelper: CurrencyExchangeHelper,
    private val networkConnectionHelper: NetworkConnectionHelper,
) : ViewModel() {

    private val currencyExchangeRateState: MutableStateFlow<CurrencyExchangeRates?> = MutableStateFlow(null)

    private val _currencyExchangeEvent = MutableSharedFlow<CurrencyExchangeEvent>()
    val currencyExchangeEvent = _currencyExchangeEvent.asSharedFlow()

    fun pollCurrencyExchangeRates() {
        viewModelScope.launch(ioCoroutineContext) {
            poller.currencyExchangeRatesPoll().collect {
                setCurrencyExchangeRateState(it)
            }
        }
    }

    fun submitClick(sellAmount: String, receiveCurrencySymbol: String, sellCurrencySymbol: String) {
        if (sellAmount.isEmpty()) {
            triggerEmptySellAmountEvent()
        } else {
            if (receiveCurrencySymbol == sellCurrencySymbol) {
                triggerIncorrectSelectedCurrencyEvent()
            } else {
                viewModelScope.launch(ioCoroutineContext) {
                    val currencyExchangeInfo = getCurrencyExchangeInfo(
                        sellAmount = sellAmount.toDouble(),
                        receiveCurrencySymbol = receiveCurrencySymbol,
                        sellCurrencySymbol = sellCurrencySymbol,
                        commissionCount = dataManager.getCommissionCount()
                    )
                    currencyExchangeInfo.exchangeRate?.let {
                        if (isSellDeductedBalanceAmountNegative(dataManager.getBalance(), currencyExchangeInfo)) {
                            triggerNegativeBalanceEvent()
                        } else {
                            triggerAmountConvertSuccessEvent(currencyExchangeInfo)
                            updateAccount(currencyExchangeInfo)
                        }
                    } ?: run {
                        if (!networkConnectionHelper.isNetworkConnected()) {
                            triggerNetworkNotAvailableEvent()
                        } else {
                            triggerRateNotAvailableEvent()
                        }
                    }
                }
            }
        }
    }

    private fun getCurrencyExchangeInfo(
        sellAmount: Double,
        receiveCurrencySymbol: String,
        sellCurrencySymbol: String,
        commissionCount: Int
    ): CurrencyExchangeInfo {
        val exchangeRate = getCurrencyExchangeRate(receiveCurrencySymbol, sellCurrencySymbol)
        val receiveAmount = currencyExchangeHelper.convertCurrencyExchange(sellAmount, exchangeRate ?: 0.0)
        val commissionAmount = currencyExchangeHelper.getCommissionAmount(sellAmount, commissionCount)
        return CurrencyExchangeInfo(
            receiveAmount = receiveAmount,
            sellAmount = sellAmount,
            receiveCurrencySymbol = receiveCurrencySymbol,
            sellCurrencySymbol = sellCurrencySymbol,
            commissionAmount = commissionAmount,
            exchangeRate = exchangeRate
        )
    }

    private fun getCurrencyExchangeRate(receiveCurrencySymbol: String, sellCurrencySymbol: String): Double? {
        currencyExchangeRateState.value?.let { currencyExchangeRate ->
            if (sellCurrencySymbol == currencyExchangeRate.base) {
                return when (receiveCurrencySymbol) {
                    context.getString(EUR.res) -> {
                        currencyExchangeRate.rates.eur
                    }
                    context.getString(USD.res) -> {
                        currencyExchangeRate.rates.usd
                    }
                    context.getString(BGN.res) -> {
                        currencyExchangeRate.rates.bgn
                    }
                    else -> { null }
                }
            }
        }
        return null
    }

    private fun updateAccount(currencyExchangeInfo: CurrencyExchangeInfo) {
        viewModelScope.launch(ioCoroutineContext) {
            val balance = dataManager.getBalance()
            val commissionCount = dataManager.getCommissionCount()

            when (currencyExchangeInfo.receiveCurrencySymbol) {
                context.getString(EUR.res) -> {
                    balance.eur += currencyExchangeInfo.receiveAmount
                }
                context.getString(USD.res) -> {
                    balance.usd += currencyExchangeInfo.receiveAmount
                }
                context.getString(BGN.res) -> {
                    balance.bgn += currencyExchangeInfo.receiveAmount
                }
            }
            when (currencyExchangeInfo.sellCurrencySymbol) {
                context.getString(EUR.res) -> {
                    balance.eur -= (currencyExchangeInfo.sellAmount - currencyExchangeInfo.commissionAmount)
                }
                context.getString(USD.res) -> {
                    balance.usd -= (currencyExchangeInfo.sellAmount - currencyExchangeInfo.commissionAmount)
                }
                context.getString(BGN.res) -> {
                    balance.bgn -= (currencyExchangeInfo.sellAmount  - currencyExchangeInfo.commissionAmount)
                }
            }
            dataManager.saveBalance(balance)
            dataManager.saveCommissionCount(commissionCount + 1)
        }
    }

    private fun isSellDeductedBalanceAmountNegative(
        balance: Balance,
        currencyExchangeInfo: CurrencyExchangeInfo
    ): Boolean {
        val sellDeductedBalanceAmount = when (currencyExchangeInfo.sellCurrencySymbol) {
            context.getString(EUR.res) -> {
                balance.eur - currencyExchangeInfo.sellAmount - currencyExchangeInfo.commissionAmount
            }
            context.getString(USD.res) -> {
                balance.usd - currencyExchangeInfo.sellAmount -  currencyExchangeInfo.commissionAmount
            }
            context.getString(BGN.res) -> {
                balance.bgn - currencyExchangeInfo.sellAmount -  currencyExchangeInfo.commissionAmount
            }
            else -> 0.0
        }
        return sellDeductedBalanceAmount < 0
    }

    fun formatCurrencyExchange(amount: Double, res: Int): String {
        return currencyExchangeHelper.formatCurrencyExchange(amount, context.getString(res))
    }

    private fun triggerEmptySellAmountEvent() {
        viewModelScope.launch {
            _currencyExchangeEvent.emit(
                CurrencyExchangeEvent.EmptySellAmount(
                    context.getString(R.string.empty_sell_amount)
                )
            )
        }
    }

    private fun triggerIncorrectSelectedCurrencyEvent() {
        viewModelScope.launch {
            _currencyExchangeEvent.emit(
                CurrencyExchangeEvent.IncorrectSelectedCurrency(
                    context.getString(R.string.incorrect_selected_currency)
                )
            )
        }
    }

    private fun triggerRateNotAvailableEvent() {
        viewModelScope.launch {
            _currencyExchangeEvent.emit(
                CurrencyExchangeEvent.RateNotAvailable(
                    context.getString(R.string.exchange_rate_not_available)
                )
            )
        }
    }

    private fun triggerNetworkNotAvailableEvent() {
        viewModelScope.launch {
            _currencyExchangeEvent.emit(
                CurrencyExchangeEvent.NetworkNotAvailable(
                    context.getString(R.string.network_not_available)
                )
            )
        }
    }

    private fun triggerNegativeBalanceEvent() {
        viewModelScope.launch {
            _currencyExchangeEvent.emit(
                CurrencyExchangeEvent.NegativeBalance(
                    context.getString(R.string.balance_below_zero)
                )
            )
        }
    }

    private fun triggerAmountConvertSuccessEvent(currencyExchangeInfo: CurrencyExchangeInfo) {
        viewModelScope.launch {
            val sellAmountFormatted = currencyExchangeHelper.formatCurrencyExchange(
                currencyExchangeInfo.sellAmount,
                currencyExchangeInfo.sellCurrencySymbol
            )
            val receiveAmountFormatted = currencyExchangeHelper.formatCurrencyExchange(
                currencyExchangeInfo.receiveAmount,
                currencyExchangeInfo.receiveCurrencySymbol
            )
            val message = if (currencyExchangeInfo.commissionAmount == 0.0) {
                String.format(context.getString(R.string.currency_convert_success_commission_free),
                    sellAmountFormatted, receiveAmountFormatted)
            } else {
                val commissionAmountFormatted = currencyExchangeHelper.formatCurrencyExchange(
                    currencyExchangeInfo.commissionAmount,
                    currencyExchangeInfo.sellCurrencySymbol
                )
                String.format(context.getString(R.string.currency_convert_success),
                    sellAmountFormatted, receiveAmountFormatted, commissionAmountFormatted)
            }
            _currencyExchangeEvent.emit(
                CurrencyExchangeEvent.ConvertedAmountSuccess(
                    message = message,
                    convertedAmount = "+" + currencyExchangeInfo.receiveAmount.toString()
                )
            )
        }
    }

    fun setCurrencyExchangeRateState(value: CurrencyExchangeRates?) {
        currencyExchangeRateState.value = value
    }

    suspend fun getBalanceFlow() = dataManager.getBalanceFlow()

    sealed class CurrencyExchangeEvent {
        class EmptySellAmount(var message: String) : CurrencyExchangeEvent()
        class IncorrectSelectedCurrency(var message: String) : CurrencyExchangeEvent()
        class RateNotAvailable(var message: String) : CurrencyExchangeEvent()
        class NetworkNotAvailable(var message: String) : CurrencyExchangeEvent()
        class NegativeBalance(var message: String) : CurrencyExchangeEvent()
        class ConvertedAmountSuccess(
            var message: String,
            var convertedAmount: String
        ) : CurrencyExchangeEvent()
    }
}