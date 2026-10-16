package com.tainoapp.wallet.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class DateUtilsTest {
    @Test
    fun monthlyPeriodNameIsEnglishWhenDeviceLocaleIsSwedish() {
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("sv-SE"))

            assertEquals("October 2026", DateUtils.parsePeriodKey("M-2026-10"))
        } finally {
            Locale.setDefault(originalLocale)
        }
    }
}
