package com.tainoapp.wallet.util

import com.tainoapp.wallet.data.BudgetCycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val monthDayFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

    private fun getDayOfMonthWithSuffix(day: Int): String {
        if (day in 11..13) {
            return "${day}th"
        }
        return when (day % 10) {
            1 -> "${day}st"
            2 -> "${day}nd"
            3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    fun getPeriodKey(date: Date, cycle: BudgetCycle): String {
        val calendar = Calendar.getInstance().apply { time = date }
        val year = calendar.get(Calendar.YEAR)

        return when (cycle) {
            BudgetCycle.WEEKLY -> {
                val week = calendar.get(Calendar.WEEK_OF_YEAR)
                "W-$year-$week"
            }
            BudgetCycle.BI_WEEKLY -> {
                val week = calendar.get(Calendar.WEEK_OF_YEAR)
                val biWeek = (week + 1) / 2
                "B-$year-$biWeek"
            }
            BudgetCycle.MONTHLY -> {
                val month = calendar.get(Calendar.MONTH) + 1
                "M-$year-${String.format("%02d", month)}"
            }
        }
    }

    fun parsePeriodKey(key: String): String {
        val parts = key.split("-")
        if (parts.size != 3) return "Invalid Period"

        val year = parts[1].toIntOrNull() ?: return "Invalid Year"
        val periodNum = parts[2].toIntOrNull() ?: return "Invalid Period Number"

        return when (parts[0]) {
            "W" -> {
                val cal = Calendar.getInstance().apply {
                    clear()
                    firstDayOfWeek = Calendar.MONDAY // Corrected
                    set(Calendar.YEAR, year)
                    set(Calendar.WEEK_OF_YEAR, periodNum)
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Corrected
                }
                val startDate = cal.time
                val startDayString = getDayOfMonthWithSuffix(cal.get(Calendar.DAY_OF_MONTH))
                val startMonthString = SimpleDateFormat("MMMM", Locale.getDefault()).format(startDate)
                cal.add(Calendar.DATE, 6)
                val endDate = cal.time
                "Week of $startMonthString $startDayString (${monthDayFormat.format(startDate)} - ${monthDayFormat.format(endDate)})"
            }
            "B" -> {
                val startWeek = (periodNum - 1) * 2 + 1
                val cal = Calendar.getInstance().apply {
                    clear()
                    firstDayOfWeek = Calendar.MONDAY // Corrected
                    set(Calendar.YEAR, year)
                    set(Calendar.WEEK_OF_YEAR, startWeek)
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Corrected
                }
                val startDate = cal.time
                val startDayString = getDayOfMonthWithSuffix(cal.get(Calendar.DAY_OF_MONTH))
                val startMonthString = SimpleDateFormat("MMMM", Locale.getDefault()).format(startDate)
                cal.add(Calendar.DATE, 13)
                val endDate = cal.time
                "Weeks of $startMonthString $startDayString (${monthDayFormat.format(startDate)} - ${monthDayFormat.format(endDate)})"
            }
            "M" -> {
                val cal = Calendar.getInstance().apply {
                    clear()
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, periodNum - 1)
                }
                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
            }
            else -> "Unknown Period"
        }
    }
}
