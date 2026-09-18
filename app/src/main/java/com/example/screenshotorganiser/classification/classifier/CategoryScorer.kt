package com.example.screenshotorganiser.classification.classifier

/**
 * Holds the calculated evidence scores for each category.
 */
data class CategoryScores(
    val eventScore: Int,
    val receiptScore: Int,
    val contactScore: Int,
    val generalScore: Int = 0
)

class CategoryScorer {

    companion object {
        // Receipt keywords and their weights
        private val RECEIPT_KEYWORDS = mapOf(
            "receipt" to 3,
            "invoice" to 3,
            "total" to 2,
            "subtotal" to 2,
            "tax" to 2,
            "gst" to 2,
            "paid" to 2,
            "transaction" to 2,
            "order" to 1
        )

        // Event keywords and their weights
        private val EVENT_KEYWORDS = mapOf(
            "event" to 3,
            "venue" to 3,
            "rsvp" to 3,
            "register" to 2,
            "registration" to 2,
            "workshop" to 2,
            "seminar" to 2,
            "conference" to 2,
            "fest" to 2,
            "schedule" to 2,
            "time" to 1
        )

        // Contact keywords and their weights
        private val CONTACT_KEYWORDS = mapOf(
            "contact" to 3,
            "mobile" to 3,
            "phone" to 3,
            "email" to 3,
            "phone number" to 3
        )

        // Extraction bonus weights
        private const val CURRENCY_BONUS = 3
        private const val DATE_BONUS = 2
        private const val PHONE_BONUS = 4
        private const val EMAIL_BONUS = 4
    }

    /**
     * Calculates category scores based on keyword presence and extracted entity features.
     *
     * @param text Preprocessed OCR text (or raw text).
     * @param detectedDates List of detected dates.
     * @param detectedCurrency List of detected currency values.
     * @param detectedPhoneNumbers List of detected phone numbers.
     * @param detectedEmails List of detected emails.
     * @return [CategoryScores] containing accumulated points for each category.
     */
    fun score(
        text: String?,
        detectedDates: List<String> = emptyList(),
        detectedCurrency: List<String> = emptyList(),
        detectedPhoneNumbers: List<String> = emptyList(),
        detectedEmails: List<String> = emptyList()
    ): CategoryScores {
        val normalizedText = text?.lowercase() ?: ""

        var receiptScore = 0
        var eventScore = 0
        var contactScore = 0

        // 1. Score keyword presence using word boundaries to prevent false substring matches
        for ((keyword, weight) in RECEIPT_KEYWORDS) {
            if (containsWord(normalizedText, keyword)) {
                receiptScore += weight
            }
        }

        for ((keyword, weight) in EVENT_KEYWORDS) {
            if (containsWord(normalizedText, keyword)) {
                eventScore += weight
            }
        }

        for ((keyword, weight) in CONTACT_KEYWORDS) {
            if (containsWord(normalizedText, keyword)) {
                contactScore += weight
            }
        }

        // 2. Score extracted entities
        if (detectedCurrency.isNotEmpty()) {
            receiptScore += CURRENCY_BONUS
        }

        if (detectedDates.isNotEmpty()) {
            eventScore += DATE_BONUS
        }

        if (detectedPhoneNumbers.isNotEmpty()) {
            contactScore += PHONE_BONUS
        }

        if (detectedEmails.isNotEmpty()) {
            contactScore += EMAIL_BONUS
        }

        return CategoryScores(
            eventScore = eventScore,
            receiptScore = receiptScore,
            contactScore = contactScore,
            generalScore = 0
        )
    }

    /**
     * Checks if the given [keyword] occurs as a distinct word or phrase in [text].
     */
    private fun containsWord(text: String, keyword: String): Boolean {
        if (text.isEmpty()) return false
        val pattern = Regex("""\b${Regex.escape(keyword)}\b""", RegexOption.IGNORE_CASE)
        return pattern.containsMatchIn(text)
    }
}
