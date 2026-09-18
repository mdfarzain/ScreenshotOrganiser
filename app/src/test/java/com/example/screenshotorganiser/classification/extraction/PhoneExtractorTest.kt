package com.example.screenshotorganiser.classification.extraction

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneExtractorTest {

    private val extractor = PhoneExtractor()

    @Test
    fun extract_plainTenDigits_returnsPhoneNumber() {
        val input = "9876543210"
        val expected = listOf("9876543210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_fiveFiveWithSpace_returnsPhoneNumber() {
        val input = "Call 98765 43210"
        val expected = listOf("98765 43210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_fiveFiveWithHyphen_returnsPhoneNumber() {
        val input = "Call 98765-43210"
        val expected = listOf("98765-43210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_plusNinetyOneWithSpace_returnsPhoneNumber() {
        val input = "+91 9876543210"
        val expected = listOf("+91 9876543210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_plusNinetyOneWithHyphen_returnsPhoneNumber() {
        val input = "+91-9876543210"
        val expected = listOf("+91-9876543210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_plusNinetyOneWithSpaceAndSplitNumber_returnsPhoneNumber() {
        val input = "+91 98765 43210"
        val expected = listOf("+91 98765 43210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_zeroNinetyOnePrefix_returnsPhoneNumber() {
        val input = "Dial 0919876543210 for help"
        val expected = listOf("0919876543210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_multiplePhoneNumbersInOneText_preservesOrderAndExtractsAll() {
        val input = "Reach out at 9876543210 or +91 98765 43210 anytime"
        val expected = listOf("9876543210", "+91 98765 43210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_duplicatePhoneNumbers_returnsDistinctResult() {
        val input = "Call 9876543210 or 9876543210 for confirmation"
        val expected = listOf("9876543210")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_datesTimesAmountsPinCodesAndQuantities_doesNotMatchFalsePositives() {
        val input = "Meeting on 18/09/2026 at 10:30 AM with 5 people. Total: ₹450. PIN: 600127, year 2026, qty 12345."
        val expected = emptyList<String>()
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_nullAndEmptyInput_returnsEmptyList() {
        assertEquals(emptyList<String>(), extractor.extract(null))
        assertEquals(emptyList<String>(), extractor.extract(""))
        assertEquals(emptyList<String>(), extractor.extract("   \t  \n "))
    }
}
