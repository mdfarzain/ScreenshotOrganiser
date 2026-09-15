package com.example.screenshotorganiser.data

import java.util.Calendar

object ExpiryEngine {

    fun calculateExpiryDate(
        category: String?,
        baseTimeMillis: Long = System.currentTimeMillis()
    ): Long? {

        val shelfLifeDays = ShelfLifeRules.getShelfLifeDays(category)

        if (shelfLifeDays < 0) {
            return null
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = baseTimeMillis
            add(Calendar.DAY_OF_YEAR, shelfLifeDays)
        }

        return calendar.timeInMillis
    }

    fun isExpired(
        expiryDate: Long?,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): Boolean {
        return expiryDate != null && expiryDate <= currentTimeMillis
    }
}