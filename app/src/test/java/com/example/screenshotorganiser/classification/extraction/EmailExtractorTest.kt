package com.example.screenshotorganiser.classification.extraction

import org.junit.Assert.assertEquals
import org.junit.Test

class EmailExtractorTest {

    private val extractor = EmailExtractor()

    @Test
    fun extract_basicEmail_returnsEmail() {
        val input = "Please write to test@example.com for queries."
        val expected = listOf("test@example.com")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_emailContainingDots_returnsEmail() {
        val input = "Contact first.last@example.org"
        val expected = listOf("first.last@example.org")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_emailContainingNumbers_returnsEmail() {
        val input = "Send your submission to abc123@gmail.com today"
        val expected = listOf("abc123@gmail.com")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_emailWithSubdomain_returnsEmail() {
        val input = "Official contact: student.name@vit.ac.in"
        val expected = listOf("student.name@vit.ac.in")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_emailWithPlusTag_returnsEmail() {
        val input = "Feedback sent from user+test@example.com"
        val expected = listOf("user+test@example.com")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_multipleEmails_preservesOrderAndExtractsAll() {
        val input = "Reach out to support@company.com or sales@store.co.in"
        val expected = listOf("support@company.com", "sales@store.co.in")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_duplicateEmails_returnsDistinctList() {
        val input = "Primary: admin@domain.com, Backup: admin@domain.com"
        val expected = listOf("admin@domain.com")
        assertEquals(expected, extractor.extract(input))
    }

    @Test
    fun extract_invalidAndIncompleteEmails_doesNotMatch() {
        val input = "Invalid cases: user@ and @gmail.com and just_a_word and https://example.com"
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
    fun extract_phoneDateCurrencyWithoutEmail_returnsEmptyList() {
        val input = "Call +91 98765 43210 on 18/09/2026. Paid ₹450.00 for order 12345."
        val expected = emptyList<String>()
        assertEquals(expected, extractor.extract(input))
    }
}
