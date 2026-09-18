package com.example.screenshotorganiser.classification.preprocessing

class TextPreprocessor {

    companion object {
        private val WHITESPACE_REGEX = Regex("\\s+")
    }

    /**
     * Normalizes raw OCR text for downstream extraction and classification.
     *
     * - Returns an empty string if [rawText] is null or blank.
     * - Trims leading and trailing whitespace.
     * - Converts text to lowercase for consistent matching.
     * - Collapses repeated whitespace characters (spaces, tabs, newlines) into a single space.
     * - Preserves all punctuation, digits, currency symbols, and date/contact characters.
     */
    fun preprocess(rawText: String?): String {
        if (rawText.isNullOrBlank()) {
            return ""
        }

        return rawText
            .trim()
            .lowercase()
            .replace(WHITESPACE_REGEX, " ")
    }
}
