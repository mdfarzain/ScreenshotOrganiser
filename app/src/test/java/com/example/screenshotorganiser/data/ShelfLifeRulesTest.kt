package com.example.screenshotorganiser.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ShelfLifeRulesTest {

    @Test
    fun getShelfLifeDays_eventCategory_returnsOneDay() {
        assertEquals(1, ShelfLifeRules.getShelfLifeDays("EVENT"))
        assertEquals(1, ShelfLifeRules.getShelfLifeDays("event"))
        assertEquals(1, ShelfLifeRules.getShelfLifeDays("Event"))
    }

    @Test
    fun getShelfLifeDays_receiptCategory_returnsThirtyDays() {
        assertEquals(30, ShelfLifeRules.getShelfLifeDays("RECEIPT"))
        assertEquals(30, ShelfLifeRules.getShelfLifeDays("receipt"))
        assertEquals(30, ShelfLifeRules.getShelfLifeDays("Receipt"))
    }

    @Test
    fun getShelfLifeDays_contactCategory_returnsNegativeOne() {
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("CONTACT"))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("contact"))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("Contact"))
    }

    @Test
    fun getShelfLifeDays_generalCategory_returnsNegativeOne() {
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("GENERAL"))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("general"))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("General"))
    }

    @Test
    fun getShelfLifeDays_nullOrUnknownCategory_returnsNegativeOne() {
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays(null))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("UNKNOWN"))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays("RANDOM"))
        assertEquals(-1, ShelfLifeRules.getShelfLifeDays(""))
    }
}
