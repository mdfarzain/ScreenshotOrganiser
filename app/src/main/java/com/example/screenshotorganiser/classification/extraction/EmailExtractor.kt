package com.example.screenshotorganiser.classification.extraction

class EmailExtractor {

    companion object {
        /**
         * Practical regex for real-world email extraction:
         * 1. \b: Word boundary to anchor the start of the email.
         * 2. [A-Za-z0-9._%+-]+: Local part (username) supporting alphanumeric characters, dots, plus tags, etc.
         * 3. @: Required separator.
         * 4. [A-Za-z0-9.-]+: Domain labels and subdomains (e.g., vit.ac, gmail, example).
         * 5. \.[A-Za-z]{2,}: Top-level domain (TLD) requiring at least two letters (e.g., .com, .in, .org).
         * 6. \b: Word boundary to prevent capturing trailing punctuation like sentence periods.
         */
        private val EMAIL_REGEX = Regex(
            """\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b"""
        )
    }

    /**
     * Extracts email addresses from the given [text].
     *
     * - Safely returns an empty list for null, empty, or blank input.
     * - Preserves the matched email string.
     * - Returns distinct matches in their order of appearance.
     * - Rejects invalid formats (e.g., missing '@', incomplete addresses, standalone domains).
     */
    fun extract(text: String?): List<String> {
        if (text.isNullOrBlank()) {
            return emptyList()
        }

        return EMAIL_REGEX
            .findAll(text)
            .map { it.value.trim() }
            .distinct()
            .toList()
    }
}
