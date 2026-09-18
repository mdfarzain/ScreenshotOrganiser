package com.example.screenshotorganiser.classification.extraction

import org.junit.Assert.assertEquals
import org.junit.Test

class DateExtractorTest {

    private val extractor = DateExtractor()

    @Test
    fun extract_slashDateFormat_returnsDate() {
        val input = "Workshop on 18/09/2026 at 10:30 AM"
        val expected = listOf("18/09/2026")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_textualMonthDate_returnsDate() {
        val input = "Event: 18 Sep 2026\nVenue: VIT Chennai"
        val expected = listOf("18 Sep 2026")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_multipleFormatsInSingleString_preservesOrderAndExtractsBoth() {
        val input = "Start: 18-09-2026 and End: 2026-09-20"
        val expected = listOf("18-09-2026", "2026-09-20")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_dotAndShortYearFormats_returnsDates() {
        val input = "Session dates: 12.05.2024 and 15/10/26"
        val expected = listOf("12.05.2024", "15/10/26")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_fullTextualMonth_returnsDate() {
        val input = "Concert on 18 September 2026 at the auditorium"
        val expected = listOf("18 September 2026")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_duplicateDates_returnsDistinctList() {
        val input = "Meeting on 18/09/2026, reminder for 18/09/2026"
        val expected = listOf("18/09/2026")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_pricesAndPhoneNumbers_doesNotExtractAsDates() {
        val input = "Total: ₹450.00, call +91-98765-43210 or 234-5678 at 10:30 AM"
        val expected = emptyList<String>()
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_nullAndEmptyInput_returnsEmptyList() {
        assertEquals(emptyList<String>(), extractor.extract(null))
        assertEquals(emptyList<String>(), extractor.extract(""))
        assertEquals(emptyList<String>(), extractor.extract("    "))
    }
}
