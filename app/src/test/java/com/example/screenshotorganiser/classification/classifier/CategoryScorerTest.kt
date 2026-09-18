package com.example.screenshotorganiser.classification.classifier

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryScorerTest {

    private val scorer = CategoryScorer()

    @Test
    fun score_receiptLikeText_receiptScoreIsHighest() {
        val text = "Tax invoice paid in full total subtotal"
        val scores = scorer.score(text)

        assertTrue(scores.receiptScore > scores.eventScore)
        assertTrue(scores.receiptScore > scores.contactScore)
        assertTrue(scores.receiptScore > scores.generalScore)
    }

    @Test
    fun score_eventLikeText_eventScoreIsHighest() {
        val text = "Workshop registration and conference schedule at venue"
        val scores = scorer.score(text)

        assertTrue(scores.eventScore > scores.receiptScore)
        assertTrue(scores.eventScore > scores.contactScore)
        assertTrue(scores.eventScore > scores.generalScore)
    }

    @Test
    fun score_contactLikeText_contactScoreIsHighest() {
        val text = "Please reach out to our contact mobile and email"
        val scores = scorer.score(text)

        assertTrue(scores.contactScore > scores.receiptScore)
        assertTrue(scores.contactScore > scores.eventScore)
        assertTrue(scores.contactScore > scores.generalScore)
    }

    @Test
    fun score_extractedCurrency_increasesReceiptScore() {
        val withoutCurrency = scorer.score("Payment processed")
        val withCurrency = scorer.score("Payment processed", detectedCurrency = listOf("₹450.00"))

        assertEquals(withoutCurrency.receiptScore + 3, withCurrency.receiptScore)
    }

    @Test
    fun score_extractedDate_increasesEventScore() {
        val withoutDate = scorer.score("Grand opening")
        val withDate = scorer.score("Grand opening", detectedDates = listOf("18/09/2026"))

        assertEquals(withoutDate.eventScore + 2, withDate.eventScore)
    }

    @Test
    fun score_extractedPhoneAndEmail_increasesContactScore() {
        val withoutEntities = scorer.score("Customer desk")
        val withEntities = scorer.score(
            "Customer desk",
            detectedPhoneNumbers = listOf("+91 98765 43210"),
            detectedEmails = listOf("support@example.com")
        )

        // phone +4, email +4 = +8
        assertEquals(withoutEntities.contactScore + 8, withEntities.contactScore)
    }

    @Test
    fun score_genericText_producesZeroScores() {
        val text = "The quick brown fox jumps over the lazy dog"
        val scores = scorer.score(text)

        assertEquals(0, scores.receiptScore)
        assertEquals(0, scores.eventScore)
        assertEquals(0, scores.contactScore)
        assertEquals(0, scores.generalScore)
    }

    @Test
    fun score_multipleSignals_accumulateCorrectly() {
        // "event" (+3) + "workshop" (+2) + detected date (+2) = 7
        val text = "Annual event and workshop"
        val scores = scorer.score(text, detectedDates = listOf("18/09/2026"))

        assertEquals(7, scores.eventScore)
    }

    @Test
    fun score_nullAndEmptyInput_returnsAllZeroScores() {
        val nullScores = scorer.score(null)
        assertEquals(0, nullScores.receiptScore)
        assertEquals(0, nullScores.eventScore)
        assertEquals(0, nullScores.contactScore)
        assertEquals(0, nullScores.generalScore)

        val emptyScores = scorer.score("")
        assertEquals(0, emptyScores.receiptScore)
        assertEquals(0, emptyScores.eventScore)
        assertEquals(0, emptyScores.contactScore)
        assertEquals(0, emptyScores.generalScore)
    }
}
