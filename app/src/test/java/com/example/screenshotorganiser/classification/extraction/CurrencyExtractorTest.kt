package com.example.screenshotorganiser.classification.extraction

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyExtractorTest {

    private val extractor = CurrencyExtractor()

    @Test
    fun extract_rupeeSymbolInteger_returnsCurrency() {
        val input = "Total amount is ₹450 paid successfully"
        val expected = listOf("₹450")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_rupeeSymbolWithCommaAndDecimals_returnsCurrency() {
        val input = "Grand Total: ₹1,299.50 (incl. taxes)"
        val expected = listOf("₹1,299.50")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_rsWithDot_returnsCurrency() {
        val input = "Bill: Rs. 450 due by Friday"
        val expected = listOf("Rs. 450")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_inrCode_returnsCurrency() {
        val input = "Payment received: INR 450 via UPI"
        val expected = listOf("INR 450")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_multipleCurrencyValues_preservesOrderAndExtractsAll() {
        val input = "Subtotal ₹450, Delivery charges Rs. 50.00, Grand Total INR 500"
        val expected = listOf("₹450", "Rs. 50.00", "INR 500")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_ordinaryNumbersWithoutCurrency_returnsEmptyList() {
        val input = "Call +91-98765-43210 at 10:30 AM on 18/09/2026 for 5 items with code 600127"
        val expected = emptyList<String>()
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_nullAndEmptyInput_returnsEmptyList() {
        assertEquals(emptyList<String>(), extractor.extract(null))
        assertEquals(emptyList<String>(), extractor.extract(""))
        assertEquals(emptyList<String>(), extractor.extract("   \t \n  "))
    }

    @Test
    fun extract_preprocessedLowerCaseInputs_returnsCurrency() {
        val input = "total ₹450.00 paid with rs 450 and inr 1,299.50"
        val expected = listOf("₹450.00", "rs 450", "inr 1,299.50")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_duplicateCurrencyValues_returnsDistinct() {
        val input = "Amount: ₹450 paid. Balance: ₹450"
        val expected = listOf("₹450")
        assertEquals(expected, extractor.extract(input))
    }
}
