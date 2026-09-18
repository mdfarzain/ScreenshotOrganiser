package com.example.screenshotorganiser.classification.extraction

class CurrencyExtractor {

    companion object {
        /**
         * Pattern matches:
         * 1. Currency indicator:
         *    - Currency symbols: ₹, $, €, £ (with optional space)
         *    - Currency abbreviations: Rs., Rs, INR with word boundary (with optional space)
         * 2. Number:
         *    - Standard or Indian comma-separated digits or plain digits (\d+(?:,\d{2,3})*)
         *    - Optional decimal part with 1-2 digits ((?:\.\d{1,2})?)
         *    - Word/digit boundary lookahead ((?!\d))
         */
        private val CURRENCY_REGEX = Regex(
            """(?:[₹$€£]\s*|\b(?:rs\.?|inr)\s*)\d+(?:,\d{2,3})*(?:\.\d{1,2})?(?!\d)""",
            RegexOption.IGNORE_CASE
        )
    }

    /**
     * Extracts currency/monetary representations from the given [text].
     *
     * - Safely returns an empty list for null, empty, or blank text.
     * - Preserves the matched text representation (e.g., "₹450.00", "Rs. 450", "INR 450").
     * - Returns distinct matches in their order of appearance.
     * - Ignores standalone numbers like phone numbers, years, quantities, and dates.
     */
    fun extract(text: String?): List<String> {
        if (text.isNullOrBlank()) {
            return emptyList()
        }

        return CURRENCY_REGEX
            .findAll(text)
            .map { it.value.trim() }
            .distinct()
            .toList()
    }
}
