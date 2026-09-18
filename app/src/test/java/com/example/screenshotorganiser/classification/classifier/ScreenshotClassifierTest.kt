package com.example.screenshotorganiser.classification.classifier

import com.example.screenshotorganiser.classification.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenshotClassifierTest {

    private val classifier = ScreenshotClassifier()

    @Test
    fun classify_strongReceiptText_returnsReceiptCategory() {
        val input = "Invoice Total ₹450 GST Paid"
        val result = classifier.classify(input)

        assertEquals(Category.RECEIPT, result.category)
        assertTrue(result.confidence > 0.5f)
        assertEquals(listOf("₹450"), result.detectedCurrency)
    }

    @Test
    fun classify_strongEventText_returnsEventCategory() {
        val input = "Workshop Venue Registration 18/09/2026"
        val result = classifier.classify(input)

        assertEquals(Category.EVENT, result.category)
        assertTrue(result.confidence > 0.5f)
        assertEquals(listOf("18/09/2026"), result.detectedDates)
    }

    @Test
    fun classify_strongContactText_returnsContactCategory() {
        val input = "Contact Mobile 9876543210 Email test@example.com"
        val result = classifier.classify(input)

        assertEquals(Category.CONTACT, result.category)
        assertTrue(result.confidence > 0.5f)
        assertEquals(listOf("9876543210"), result.detectedPhoneNumbers)
        assertEquals(listOf("test@example.com"), result.detectedEmails)
    }

    @Test
    fun classify_genericText_returnsGeneralWithZeroConfidence() {
        val input = "The quick brown fox jumps over the lazy dog"
        val result = classifier.classify(input)

        assertEquals(Category.GENERAL, result.category)
        assertEquals(0f, result.confidence, 0.001f)
    }

    @Test
    fun classify_nullOrBlankInput_returnsGeneralWithZeroConfidence() {
        val nullResult = classifier.classify(null)
        assertEquals(Category.GENERAL, nullResult.category)
        assertEquals(0f, nullResult.confidence, 0.001f)
        assertEquals("", nullResult.ocrText)

        val emptyResult = classifier.classify("")
        assertEquals(Category.GENERAL, emptyResult.category)
        assertEquals(0f, emptyResult.confidence, 0.001f)

        val blankResult = classifier.classify("   \t\n  ")
        assertEquals(Category.GENERAL, blankResult.category)
        assertEquals(0f, blankResult.confidence, 0.001f)
    }

    @Test
    fun classify_entitiesAffectClassificationCorrectly() {
        // Only currency present -> pushes toward RECEIPT
        val currencyResult = classifier.classify("Amount paid: ₹1,299.50")
        assertEquals(Category.RECEIPT, currencyResult.category)
        assertTrue(currencyResult.detectedCurrency.isNotEmpty())

        // Only date present -> pushes toward EVENT
        val dateResult = classifier.classify("Important happening on 18/09/2026")
        assertEquals(Category.EVENT, dateResult.category)
        assertTrue(dateResult.detectedDates.isNotEmpty())

        // Phone and email present -> pushes toward CONTACT
        val contactResult = classifier.classify("Call 9876543210 or email info@test.com")
        assertEquals(Category.CONTACT, contactResult.category)
        assertTrue(contactResult.detectedPhoneNumbers.isNotEmpty())
        assertTrue(contactResult.detectedEmails.isNotEmpty())
    }

    @Test
    fun classify_confidenceCalculation_matchesProportionalFormula() {
        // "event" (+3) and "order" (+1). Total = 4.
        // Winning category: EVENT (3/4 = 0.75)
        val input = "event order"
        val result = classifier.classify(input)

        assertEquals(Category.EVENT, result.category)
        assertEquals(0.75f, result.confidence, 0.001f)
    }

    @Test
    fun classify_tieScores_returnsGeneralWithZeroConfidence() {
        // "event" (+3) and "invoice" (+3). Total = 6.
        // Tie between EVENT (3) and RECEIPT (3).
        val input = "event invoice"
        val result = classifier.classify(input)

        assertEquals(Category.GENERAL, result.category)
        assertEquals(0f, result.confidence, 0.001f)
    }

    @Test
    fun classify_classificationResultContainsAllExtractedFields() {
        val input = "  Event on 18/09/2026. Entry ₹450. Contact 9876543210 or email test@example.com  "
        val result = classifier.classify(input)

        // Preprocessed text is trimmed and single-spaced
        assertEquals("event on 18/09/2026. entry ₹450. contact 9876543210 or email test@example.com", result.ocrText)
        assertEquals(listOf("18/09/2026"), result.detectedDates)
        assertEquals(listOf("₹450"), result.detectedCurrency)
        assertEquals(listOf("9876543210"), result.detectedPhoneNumbers)
        assertEquals(listOf("test@example.com"), result.detectedEmails)
    }
}
