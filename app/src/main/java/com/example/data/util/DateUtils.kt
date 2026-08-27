package com.example.data.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    val IST_TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Kolkata")
    private val INDIA_LOCALE = Locale.forLanguageTag("en-IN")

    fun getTodayStartAndEndTimestamps(calendarOffsetDays: Int = 0): Pair<Long, Long> {
        val cal = Calendar.getInstance(IST_TIMEZONE, INDIA_LOCALE)
        if (calendarOffsetDays != 0) {
            cal.add(Calendar.DAY_OF_YEAR, calendarOffsetDays)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getDateRangeForDays(daysBack: Int): Pair<Long, Long> {
        val endCal = Calendar.getInstance(IST_TIMEZONE, INDIA_LOCALE)
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val end = endCal.timeInMillis

        val startCal = Calendar.getInstance(IST_TIMEZONE, INDIA_LOCALE)
        startCal.add(Calendar.DAY_OF_YEAR, -daysBack + 1)
        startCal.set(Calendar.HOUR_OF_DAY, 0)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        startCal.set(Calendar.MILLISECOND, 0)
        val start = startCal.timeInMillis

        return Pair(start, end)
    }

    fun getMonthStartAndEndTimestamps(): Pair<Long, Long> {
        val cal = Calendar.getInstance(IST_TIMEZONE, INDIA_LOCALE)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        val endCal = Calendar.getInstance(IST_TIMEZONE, INDIA_LOCALE)
        endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val end = endCal.timeInMillis

        return Pair(start, end)
    }

    fun getCustomDayTimestamps(year: Int, month: Int, day: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance(IST_TIMEZONE, INDIA_LOCALE)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    fun getTodayDateKey(): String {
        return formatDateKey(System.currentTimeMillis())
    }

    fun formatDateKey(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date(timestamp))
    }

    fun formatDisplayDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date(timestamp))
    }

    fun formatDisplayTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date(timestamp))
    }

    fun formatFullDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", INDIA_LOCALE)
        sdf.timeZone = IST_TIMEZONE
        return sdf.format(Date(timestamp))
    }

    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(INDIA_LOCALE)
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 0
        return "₹${formatter.format(amount)}"
    }

    fun isWithin15Minutes(timestamp: Long): Boolean {
        return isWithin1Point5Hours(timestamp)
    }

    fun isWithin1Point5Hours(timestamp: Long): Boolean {
        val ninetyMinutesMillis = 90 * 60 * 1000L
        val diff = System.currentTimeMillis() - timestamp
        return diff in 0..ninetyMinutesMillis
    }

    fun isWithin1Hour(timestamp: Long): Boolean {
        return isWithin2Hours(timestamp)
    }

    fun isWithin2Hours(timestamp: Long): Boolean {
        val twoHoursMillis = 2 * 60 * 60 * 1000L
        val diff = System.currentTimeMillis() - timestamp
        return diff in 0..twoHoursMillis
    }

    fun getRemainingLockMinutes(timestamp: Long): Int {
        val ninetyMinutesMillis = 90 * 60 * 1000L
        val diff = System.currentTimeMillis() - timestamp
        val remainingMillis = ninetyMinutesMillis - diff
        return if (remainingMillis > 0) {
            (remainingMillis / 60000).toInt() + 1
        } else {
            0
        }
    }
}
