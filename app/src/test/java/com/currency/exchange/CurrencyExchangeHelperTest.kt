package com.currency.exchange

import com.currency.exchange.util.CurrencyExchangeHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyExchangeHelperTest {

    private var currencyExchangeHelper = CurrencyExchangeHelper()

    @Test
    fun testCurrencyExchangeConversion() {
        val conversionAmount = currencyExchangeHelper.convertCurrencyExchange(300.0, 12.0)
        assertEquals(3600.0, conversionAmount, 0.0)
    }

    @Test
    fun testCurrencyExchangeFormat() {
        val conversionAmount = currencyExchangeHelper.formatCurrencyExchange(300.167897, "USD")
        assertEquals("300.17 USD", conversionAmount)
    }

    @Test
    fun testCommissionWhenAmountIsNotAboveLimit() {
        val commissionAmount = currencyExchangeHelper.getCommissionAmount(100.0, 4)
        assertEquals(0.0, commissionAmount, 0.0)
    }

    @Test
    fun testCommissionWhenCommissionCountIsAboveLimit() {
        val commissionAmount = currencyExchangeHelper.getCommissionAmount(300.00, 7)
        assertEquals(2.1, commissionAmount, 0.0)
    }

    @Test
    fun testCommissionWhenCommissionCountAndAmountAreAboveLimit() {
        val commissionAmount = currencyExchangeHelper.getCommissionAmount(300.00, 12)
        assertEquals(2.1, commissionAmount, 0.0)
    }

    @Test
    fun testCommissionEvery10thConversion() {
        val commissionAmount = currencyExchangeHelper.getCommissionAmount(300.00, 20)
        assertEquals(0.0, commissionAmount, 0.0)
    }
}