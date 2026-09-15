package com.example.screenshotorganiser.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExpiryEngineTest {

    private val baseTimeMillis = 1774000000000L // arbitrary fixed timestamp

    @Test
    fun calculateExpiryDate_eventCategory_returnsNextDay() {
        val expiry = ExpiryEngine.calculateExpiryDate("EVENT", baseTimeMillis)
        assertNotNull(expiry)

        val expectedCalendar = Calendar.getInstance().apply {
            timeInMillis = baseTimeMillis
            add(Calendar.DAY_OF_YEAR, 1)
        }
        assertEquals(expectedCalendar.timeInMillis, expiry)
    }

    @Test
    fun calculateExpiryDate_receiptCategory_returnsThirtyDaysLater() {
        val expiry = ExpiryEngine.calculateExpiryDate("RECEIPT", baseTimeMillis)
        assertNotNull(expiry)

        val expectedCalendar = Calendar.getInstance().apply {
            timeInMillis = baseTimeMillis
            add(Calendar.DAY_OF_YEAR, 30)
        }
        assertEquals(expectedCalendar.timeInMillis, expiry)
    }

    @Test
    fun calculateExpiryDate_contactCategory_returnsNull() {
        val expiry = ExpiryEngine.calculateExpiryDate("CONTACT", baseTimeMillis)
        assertNull(expiry)
    }

    @Test
    fun calculateExpiryDate_generalCategory_returnsNull() {
        val expiry = ExpiryEngine.calculateExpiryDate("GENERAL", baseTimeMillis)
        assertNull(expiry)
    }

    @Test
    fun calculateExpiryDate_nullOrUnknownCategory_returnsNull() {
        assertNull(ExpiryEngine.calculateExpiryDate(null, baseTimeMillis))
        assertNull(ExpiryEngine.calculateExpiryDate("OTHER", baseTimeMillis))
    }

    @Test
    fun isExpired_currentTimeAfterExpiry_returnsTrue() {
        val expiry = baseTimeMillis
        val currentTime = baseTimeMillis + 1000L
        assertTrue(ExpiryEngine.isExpired(expiry, currentTime))
    }

    @Test
    fun isExpired_currentTimeEqualsExpiry_returnsTrue() {
        val expiry = baseTimeMillis
        val currentTime = baseTimeMillis
        assertTrue(ExpiryEngine.isExpired(expiry, currentTime))
    }

    @Test
    fun isExpired_currentTimeBeforeExpiry_returnsFalse() {
        val expiry = baseTimeMillis + 5000L
        val currentTime = baseTimeMillis
        assertFalse(ExpiryEngine.isExpired(expiry, currentTime))
    }

    @Test
    fun isExpired_nullExpiryDate_returnsFalse() {
        assertFalse(ExpiryEngine.isExpired(null, baseTimeMillis))
    }
}
