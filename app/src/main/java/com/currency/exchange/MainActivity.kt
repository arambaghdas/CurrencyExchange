package com.currency.exchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.currency.exchange.data.Currency.EUR
import com.currency.exchange.data.Currency.USD
import com.currency.exchange.data.Currency.BGN
import com.currency.exchange.databinding.ActivityMainBinding
import com.currency.exchange.viewmodel.CurrencyExchangeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.currency.exchange.viewmodel.CurrencyExchangeViewModel.CurrencyExchangeEvent
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private val currencyExchangeViewModel by viewModel<CurrencyExchangeViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                currencyExchangeViewModel.pollCurrencyExchangeRates()
                currencyExchangeViewModel.getBalanceFlow().collect { balance ->
                    binding.tvBalanceEur.text =
                        currencyExchangeViewModel.formatCurrencyExchange(balance.eur, EUR.res)
                    binding.tvBalanceUsd.text =
                        currencyExchangeViewModel.formatCurrencyExchange(balance.usd, USD.res)
                    binding.tvBalanceBgn.text =
                        currencyExchangeViewModel.formatCurrencyExchange(balance.bgn, BGN.res)
                }
            }
        }

        binding.btSubmit.setOnClickListener {
            val sellCurrency = binding.spSellCurrency.selectedItem.toString()
            val receiveCurrency = binding.spReceiveCurrency.selectedItem.toString()
            val amount = binding.etSellCurrency.text.toString()
            currencyExchangeViewModel.submitClick(amount, receiveCurrency, sellCurrency)
        }

        lifecycleScope.launchWhenStarted {
            currencyExchangeViewModel.currencyExchangeEvent.collectLatest { event ->
                when(event) {
                    is CurrencyExchangeEvent.EmptySellAmount -> {
                        showAlertDialog(message = event.message)
                    }
                    is CurrencyExchangeEvent.IncorrectSelectedCurrency -> {
                        showAlertDialog(message = event.message)
                    }
                    is CurrencyExchangeEvent.RateNotAvailable -> {
                        showAlertDialog(message = event.message)
                    }
                    is CurrencyExchangeEvent.NetworkNotAvailable -> {
                        showAlertDialog(message = event.message)
                    }
                    is CurrencyExchangeEvent.NegativeBalance -> {
                        showAlertDialog(message = event.message)
                    }
                    is CurrencyExchangeEvent.ConvertedAmountSuccess -> {
                        handleConvertedAmountSuccessEvent(
                            message = event.message,
                            convertedAmount = event.convertedAmount
                        )
                    }
                }
            }
        }
    }

    private fun handleConvertedAmountSuccessEvent(message: String, convertedAmount: String) {
        binding.etReceiveCurrency.setText(convertedAmount)
        showAlertDialog(getString(R.string.currency_convert_header), message)
    }

    private fun showAlertDialog(title: String = "", message: String) {
        AlertDialog.Builder(
            this
        ).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.done) { _, _ -> }
        }.show()
    }
}