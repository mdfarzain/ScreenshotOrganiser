package com.example.screenshotorganiser.data

object ShelfLifeRules {

    const val EVENT_SHELF_LIFE_DAYS = 1
    const val RECEIPT_SHELF_LIFE_DAYS = 30
    const val CONTACT_SHELF_LIFE_DAYS = -1
    const val GENERAL_SHELF_LIFE_DAYS = -1

    fun getShelfLifeDays(category: String?): Int {
        return when (category?.uppercase()) {
            "EVENT" -> EVENT_SHELF_LIFE_DAYS
            "RECEIPT" -> RECEIPT_SHELF_LIFE_DAYS
            "CONTACT" -> CONTACT_SHELF_LIFE_DAYS
            "GENERAL" -> GENERAL_SHELF_LIFE_DAYS
            else -> GENERAL_SHELF_LIFE_DAYS
        }
    }
}