package com.example.screenshotorganiser.classification.classifier

import com.example.screenshotorganiser.classification.Category
import com.example.screenshotorganiser.classification.ClassificationResult
import com.example.screenshotorganiser.classification.extraction.CurrencyExtractor
import com.example.screenshotorganiser.classification.extraction.DateExtractor
import com.example.screenshotorganiser.classification.extraction.EmailExtractor
import com.example.screenshotorganiser.classification.extraction.PhoneExtractor
import com.example.screenshotorganiser.classification.preprocessing.TextPreprocessor

class ScreenshotClassifier(
    private val preprocessor: TextPreprocessor = TextPreprocessor(),
    private val dateExtractor: DateExtractor = DateExtractor(),
    private val currencyExtractor: CurrencyExtractor = CurrencyExtractor(),
    private val phoneExtractor: PhoneExtractor = PhoneExtractor(),
    private val emailExtractor: EmailExtractor = EmailExtractor(),
    private val scorer: CategoryScorer = CategoryScorer()
) {

    /**
     * Classifies raw OCR text by executing the complete Member 1 pipeline:
     * 1. Preprocesses text.
     * 2. Extracts entities (dates, currency, phones, emails).
     * 3. Scores evidence across categories.
     * 4. Determines winning category and calculates proportional confidence.
     *
     * @param ocrText Raw OCR text from vision recognition.
     * @return [ClassificationResult] containing category, confidence, clean text, and extracted entities.
     */
    fun classify(ocrText: String?): ClassificationResult {
        if (ocrText.isNullOrBlank()) {
            return ClassificationResult(
                category = Category.GENERAL,
                confidence = 0f,
                ocrText = ""
            )
        }

        // Step 1: Preprocess OCR text
        val cleanedText = preprocessor.preprocess(ocrText)
        if (cleanedText.isBlank()) {
            return ClassificationResult(
                category = Category.GENERAL,
                confidence = 0f,
                ocrText = ""
            )
        }

        // Step 2: Extract domain entities
        val dates = dateExtractor.extract(cleanedText)
        val currencies = currencyExtractor.extract(cleanedText)
        val phoneNumbers = phoneExtractor.extract(cleanedText)
        val emails = emailExtractor.extract(cleanedText)

        // Step 3: Calculate evidence scores
        val scores = scorer.score(
            text = cleanedText,
            detectedDates = dates,
            detectedCurrency = currencies,
            detectedPhoneNumbers = phoneNumbers,
            detectedEmails = emails
        )

        val eventScore = scores.eventScore
        val receiptScore = scores.receiptScore
        val contactScore = scores.contactScore
        val totalScore = eventScore + receiptScore + contactScore

        // Step 4: If no evidence exists, fallback to GENERAL
        if (totalScore == 0) {
            return ClassificationResult(
                category = Category.GENERAL,
                confidence = 0f,
                ocrText = cleanedText,
                detectedDates = dates,
                detectedPhoneNumbers = phoneNumbers,
                detectedEmails = emails,
                detectedCurrency = currencies
            )
        }

        // Step 5: Find highest score and candidates
        val maxScore = maxOf(eventScore, receiptScore, contactScore)
        val candidates = mutableListOf<Category>()

        if (eventScore == maxScore) candidates.add(Category.EVENT)
        if (receiptScore == maxScore) candidates.add(Category.RECEIPT)
        if (contactScore == maxScore) candidates.add(Category.CONTACT)

        // Step 6: Tie Handling - if multiple categories share highest score, fallback to GENERAL with 0 confidence
        if (candidates.size > 1) {
            return ClassificationResult(
                category = Category.GENERAL,
                confidence = 0f,
                ocrText = cleanedText,
                detectedDates = dates,
                detectedPhoneNumbers = phoneNumbers,
                detectedEmails = emails,
                detectedCurrency = currencies
            )
        }

        // Step 7: Calculate confidence = winningScore / totalScore
        val winningCategory = candidates.first()
        val rawConfidence = maxScore.toFloat() / totalScore.toFloat()
        val confidence = rawConfidence.coerceIn(0f, 1f)

        return ClassificationResult(
            category = winningCategory,
            confidence = confidence,
            ocrText = cleanedText,
            detectedDates = dates,
            detectedPhoneNumbers = phoneNumbers,
            detectedEmails = emails,
            detectedCurrency = currencies
        )
    }
}
