package com.example.screenshotorganiser.classification.extraction

class DateExtractor {

    companion object {
        private val PATTERNS = listOf(
            // DD/MM/YYYY or DD/MM/YY (e.g., 18/09/2026, 18/09/26)
            Regex("""\b(?:0?[1-9]|[12]\d|3[01])/(?:0?[1-9]|1[0-2])/(?:\d{4}|\d{2})\b"""),

            // DD-MM-YYYY or DD-MM-YY (e.g., 18-09-2026, 18-09-26)
            Regex("""\b(?:0?[1-9]|[12]\d|3[01])-(?:0?[1-9]|1[0-2])-(?:\d{4}|\d{2})\b"""),

            // DD.MM.YYYY or DD.MM.YY (e.g., 18.09.2026, 18.09.26)
            Regex("""\b(?:0?[1-9]|[12]\d|3[01])\.(?:0?[1-9]|1[0-2])\.(?:\d{4}|\d{2})\b"""),

            // YYYY-MM-DD (e.g., 2026-09-18)
            Regex("""\b(?:\d{4})-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12]\d|3[01])\b"""),

            // DD Month YYYY (e.g., 18 Sep 2026, 18 September 2026)
            Regex(
                """\b(?:0?[1-9]|[12]\d|3[01])\s+(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\s+(?:\d{4})\b""",
                RegexOption.IGNORE_CASE
            ),

            // Month DD, YYYY (e.g., Sep 18, 2026, September 18 2026)
            Regex(
                """\b(?:jan(?:uary)?|feb(?:ruary)?|mar(?:ch)?|apr(?:il)?|may|jun(?:e)?|jul(?:y)?|aug(?:ust)?|sep(?:tember)?|oct(?:ober)?|nov(?:ember)?|dec(?:ember)?)\s+(?:0?[1-9]|[12]\d|3[01])(?:st|nd|rd|th)?,?\s+(?:\d{4})\b""",
                RegexOption.IGNORE_CASE
            )
        )
    }

    /**
     * Extracts date strings from the given [text].
     *
     * - Returns an empty list if [text] is null or blank.
     * - Preserves the order of appearance in the text.
     * - Removes duplicate matches.
     * - Avoids capturing prices, times, and phone numbers.
     */
    fun extract(text: String?): List<String> {
        if (text.isNullOrBlank()) {
            return emptyList()
        }

        return PATTERNS
            .flatMap { pattern -> pattern.findAll(text) }
            .sortedBy { it.range.first }
            .map { it.value }
            .distinct()
    }
}
