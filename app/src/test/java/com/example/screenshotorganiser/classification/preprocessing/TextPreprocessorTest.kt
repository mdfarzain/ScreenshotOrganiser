package com.example.screenshotorganiser.classification.preprocessing

import org.junit.Assert.assertEquals
import org.junit.Test

class TextPreprocessorTest {

    private val preprocessor = TextPreprocessor()

    @Test
    fun preprocess_receiptTextWithCurrencyAndWhitespace_normalizesCorrectly() {
        val raw = "  TOTAL   ₹450.00\n\nTHANK YOU!!! "
        val expected = "total ₹450.00 thank you!!!"
        assertEquals(expected, preprocessor.preprocess(raw))
    }

    @Test
    fun preprocess_nullAndEmptyInput_returnsEmptyString() {
        assertEquals("", preprocessor.preprocess(null))
        assertEquals("", preprocessor.preprocess(""))
        assertEquals("", preprocessor.preprocess("   \t  \n  "))
    }

    @Test
    fun preprocess_multipleLines_collapsesToSingleLineWithSpaces() {
        val raw = "Meeting at 5 PM\nRoom 204\n\nTomorrow"
        val expected = "meeting at 5 pm room 204 tomorrow"
        assertEquals(expected, preprocessor.preprocess(raw))
    }

    @Test
    fun preprocess_emailAddress_preservesEmailCharactersAndLowersCase() {
        val raw = "  Contact: Support@Example.COM   "
        val expected = "contact: support@example.com"
        assertEquals(expected, preprocessor.preprocess(raw))
    }

    @Test
    fun preprocess_phoneNumber_preservesPhoneSymbols() {
        val raw = "Call  +1 (555) 234-5678  today "
        val expected = "call +1 (555) 234-5678 today"
        assertEquals(expected, preprocessor.preprocess(raw))
    }

    @Test
    fun preprocess_dateSeparators_preservesSlashesAndDashes() {
        val raw = "Event on 12/05/2024 - 15:30 HRS"
        val expected = "event on 12/05/2024 - 15:30 hrs"
        assertEquals(expected, preprocessor.preprocess(raw))
    }
}
