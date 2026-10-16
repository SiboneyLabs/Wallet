package com.tainoapp.wallet.util

import com.tainoapp.wallet.data.BudgetCycle
import java.util.Calendar

object DateUtils {

    /**
     * Checks if a new budget period has started since the last rollover.
     * @param lastRollover The timestamp of the last rollover.
     * @param cycle The current budget cycle.
     * @return True if a new period has started, false otherwise.
     */
    fun isNewPeriod(lastRollover: Long, cycle: BudgetCycle): Boolean {
        if (lastRollover == 0L) return true // First time running

        val lastRolloverCalendar = Calendar.getInstance().apply { timeInMillis = lastRollover }
        val currentCalendar = Calendar.getInstance()

        return when (cycle) {
            BudgetCycle.WEEKLY -> {
                // Check if the week of the year or the year itself has changed.
                lastRolloverCalendar.get(Calendar.WEEK_OF_YEAR) != currentCalendar.get(Calendar.WEEK_OF_YEAR) ||
                lastRolloverCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)
            }
            BudgetCycle.BI_WEEKLY -> {
                // A simple bi-weekly check. True if more than 14 days have passed.
                // A more robust implementation would anchor this to a specific day.
                val twoWeeksInMillis = 14 * 24 * 60 * 60 * 1000
                (currentCalendar.timeInMillis - lastRollover) >= twoWeeksInMillis
            }
            BudgetCycle.MONTHLY -> {
                // Check if the month or the year has changed.
                lastRolloverCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH) ||
                lastRolloverCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)
            }
        }
    }
}
