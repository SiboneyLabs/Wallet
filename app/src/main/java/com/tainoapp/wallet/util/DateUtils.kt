package com.tainoapp.wallet.util

import com.tainoapp.wallet.data.BudgetCycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    /**
     * Generates a unique, sortable key for a given date and budget cycle.
     * e.g., "W-2024-32" for weekly, "M-2024-08" for monthly.
     */
    fun getPeriodKey(date: Date, cycle: BudgetCycle): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val year = calendar.get(Calendar.YEAR)

        return when (cycle) {
            BudgetCycle.WEEKLY -> {
                val week = calendar.get(Calendar.WEEK_OF_YEAR)
                "W-$year-$week"
            }
            BudgetCycle.BI_WEEKLY -> {
                // For bi-weekly, we can divide the week by 2 to create two-week blocks.
                val week = calendar.get(Calendar.WEEK_OF_YEAR)
                val biWeek = (week + 1) / 2
                "B-$year-$biWeek"
            }
            BudgetCycle.MONTHLY -> {
                val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-indexed
                "M-$year-${String.format("%02d", month)}"
            }
        }
    }

    /**
     * Converts a period key back into a human-readable string for display.
     */
    fun parsePeriodKey(key: String): String {
        val parts = key.split("-")
        if (parts.size != 3) return "Invalid Period"

        val year = parts[1]
        val periodNum = parts[2].toIntOrNull() ?: 0

        return when (parts[0]) {
            "W" -> "Week $periodNum, $year"
            "B" -> "Bi-Week $periodNum, $year"
            "M" -> {
                val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date(0).apply {
                    time = SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse("$year-$periodNum")?.time ?: 0
                })
                "$monthName $year"
            }
            else -> "Unknown Period"
        }
    }
    
    /**
     * Checks if a new budget period has started since the last known date.
     */
    fun isNewPeriod(lastPeriodKey: String, currentPeriodKey: String): Boolean {
        return lastPeriodKey != currentPeriodKey
    }
}
