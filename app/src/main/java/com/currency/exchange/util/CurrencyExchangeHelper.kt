package com.currency.exchange.util

import java.math.RoundingMode
import java.text.DecimalFormat

class CurrencyExchangeHelper
{
    fun convertCurrencyExchange(amount: Double, rate: Double): Double {
        return roundOffDecimal(amount * rate)
    }

    fun formatCurrencyExchange(amount: Double, currencySymbol: String): String {
        return roundOffDecimal(amount).toString() + " " + currencySymbol
    }

    fun getCommissionAmount(amount: Double, commissionCount: Int): Double {
        return if (isCommissionFree(amount, commissionCount)) {
            0.0
        } else {
            return roundOffDecimal((amount * COMMISSION_PERCENT) / 100)
        }
    }

    private fun isCommissionFree(amount: Double, commissionCount: Int): Boolean {
        return (amount < AMOUNT_LIMIT ||
                commissionCount < COMMISSION_LIMIT ||
                (commissionCount % COMMISSION_FREE == 0))
    }

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    companion object {
        const val AMOUNT_LIMIT = 200
        const val COMMISSION_LIMIT = 5
        const val COMMISSION_FREE = 10
        const val COMMISSION_PERCENT = 0.7
    }
}