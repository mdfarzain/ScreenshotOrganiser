package com.example.screenshotorganiser.classification.extraction

class PhoneExtractor {

    companion object {
        /**
         * Pattern breakdown:
         * 1. Left boundary: (?<!\w) - ensures no preceding word/digit character
         * 2. Country / Trunk Prefix (optional):
         *    (?:\+91[- ]?|091[- ]?|0[- ]?)?
         * 3. 10-digit Indian Mobile Number:
         *    - Starts with valid Indian mobile digit: [6-9]
         *    - First block of 4 digits: \d{4}
         *    - Optional separator (space or hyphen): [- ]?
         *    - Second block of 5 digits: \d{5}
         * 4. Right boundary: (?!\w) - ensures no following word/digit character
         */
        private val PHONE_REGEX = Regex(
            """(?<!\w)(?:\+91[- ]?|091[- ]?|0[- ]?)?[6-9]\d{4}[- ]?\d{5}(?!\w)"""
        )
    }

    /**
     * Extracts Indian phone numbers from the given [text].
     *
     * - Safely returns an empty list for null, empty, or blank input.
     * - Preserves the matched format (e.g., "+91 98765 43210").
     * - Returns distinct matches in their order of appearance.
     * - Ignores non-phone numbers like dates, times, amounts, PIN codes, and short numbers.
     */
    fun extract(text: String?): List<String> {
        if (text.isNullOrBlank()) {
            return emptyList()
        }

        return PHONE_REGEX
            .findAll(text)
            .map { it.value.trim() }
            .distinct()
            .toList()
    }
}
