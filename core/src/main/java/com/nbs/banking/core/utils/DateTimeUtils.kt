package com.nbs.banking.core.utils

import com.nbs.banking.core.extensions.DATE_TIME_TIMEZONE_PATTERN
import com.nbs.banking.core.extensions.DD_MMMM_YYYY_SPACED_PATTERN
import com.nbs.banking.core.extensions.MMMM_DD_YYYY_PATTERN
import com.nbs.banking.core.extensions.MM_DD_YYYY_SLASH_PATTERN
import com.nbs.banking.core.extensions.dateFormatter
import com.nbs.banking.core.extensions.emptyString
import com.nbs.banking.core.extensions.zeroString
import com.nbs.banking.core.model.LanguageEnum
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import timber.log.Timber

const val DATE_PATTERN = "ddMMyyyy"
const val DATE_TIME_PATTERN = "ddMMyyy:HH:mm:ss.SS"
const val DAY_MONTH_YEAR_PATTERN = "dd MMMM yyyy"
const val MONTH_YEAR_PATTERN = "MMMM yyyy"
const val RECEIPT_PATTERN = "dd-MM-yyyy-HH-mm-ss"

const val RFP_OUTPUT_DATE_PATTERN = "dd MMMM yyyy"
const val BANCASSURANCE_DATE_PATTERN = "dd MMMM yyyy"
const val EMANDATE_DATE_PATTERN = "dd MMMM yyyy"
const val CREDITCARD_DOB_PATTERN = "dd MMMM yyyy"
const val CREDITCARD_REQUEST_DATE_PATTERN = "yyyyMMdd"
const val BANCASSURANCE_REQUEST_DATE_PATTERN = "dd/MM/yyyy"
const val BANCASSURANCE_RESPONSE_DATE_PATTERN = "yyyyMMdd"
const val SHARIA_SAVING_PLAN_DATE_FIRST_PATTERN = "dd MMMM yyyy"
const val SHARIA_SAVING_PLAN_DATE_SECOND_PATTERN: String = "yyyy-MM-dd"
const val SHARIA_SAVING_PLAN_DATE_PATTERN: String = "ddMMyy"
const val CARDLESS_WITHDRAWAL_RESPONSE_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"
const val BONDS_RESPONSE_DATE_PATTERN = "dd/MM/yyyy"
const val BONDS_DISPLAY_DATE_PATTERN = "dd MMMM yyyy"
const val BONDS_RESPONSE_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm"
const val BONDS_DISPLAY_DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
const val BONDS_RESPONSE_BIRTH_DATE_PATTERN = "MMM dd yyyy"
const val BONDS_RESPONSE_TRANSACTION_DATE_PATTERN = "dd/MM/yyyy HH:mm:ss"
const val PORTFOLIO_RESPONSE_DATE_PATTERN = "MM/dd/yyyy HH:mm:ss a"
const val CUSTOMER_CONSENT_DATE_BEFORE = "yyyy-MM-dd HH:mm:ss.SSS"
const val CUSTOMER_CONSENT_DATE_AFTER = "d MMM yyyy, HH:mm"
const val MRZ_DATE_PATTERN = "yyMMdd"
const val INTERNET_CALL_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"
const val HOUR_MINUTE_FORMAT = "HH:mm"
const val YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

const val ONE_HOUR_MILLIS = 3_600_000L
const val ONE_MINUTE_MILLIS = 60_000L
const val ONE_SECOND_MILLIS = 1_000L

val supportedInputPatterns = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
    "yyyy-MM-dd'T'HH:mm:ss.SSSSS",
    "yyyy-MM-dd'T'HH:mm:ss.SSSS",
    "yyyy-MM-dd'T'HH:mm:ss.SSS",
    "yyyy-MM-dd'T'HH:mm:ss.SS",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd",
    "yyyy-MM-dd HH:mm:ss",
)

fun getDateToday(pattern: String = DATE_PATTERN): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return currentDate.format(formatter)
}

fun getDateTimeToday(pattern: String = DATE_TIME_PATTERN): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return currentDateTime.format(formatter)
}

fun nowIsoDateTime(): String = SimpleDateFormat(DATE_TIME_TIMEZONE_PATTERN, Locale.getDefault()).format(Date())

fun getCurrentMonth(locale: Locale, textStyle: TextStyle = TextStyle.FULL): String {
    val currentMonth = LocalDate.now().month
    return currentMonth.getDisplayName(textStyle, locale)
}

fun getMonthDateBefore(n: Long, pattern: String = DATE_PATTERN): String {
    val currentDate = LocalDate.now()
    val monthBeforeDate = currentDate.minusMonths(n)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return monthBeforeDate.format(formatter)
}

fun String?.formatDatePattern(outputPatternFormat: String, locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country)): String {
    if (this.isNullOrBlank()) return emptyString()

    val outputFormatter = DateTimeFormatter.ofPattern(outputPatternFormat, locale)

    for (pattern in supportedInputPatterns) {
        val inputFormatter = DateTimeFormatter.ofPattern(pattern, locale)
        try {
            return if (pattern == SHARIA_SAVING_PLAN_DATE_SECOND_PATTERN) {
                LocalDate.parse(this, inputFormatter).format(outputFormatter)
            } else {
                LocalDateTime.parse(this, inputFormatter).format(outputFormatter)
            }
        } catch (e: DateTimeParseException) {
            Timber.tag("formatDatePattern").d("formatDatePattern: Failed for pattern $pattern: $e")
        }
    }
    return this
}

fun formatDateToString(date: LocalDate?, pattern: String = DATE_PATTERN): String? {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return try {
        date?.format(formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun formatStringToDate(date: String, pattern: String = DATE_PATTERN): LocalDate? {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return try {
        LocalDate.parse(date, formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun isDateInRange(
    date: LocalDate,
    lowerBound: LocalDate,
    upperBound: LocalDate,
): Boolean = date.isAfter(lowerBound) && date.isBefore(upperBound)

fun getExpiredDateString(
    startDate: String,
    endDate: String,
    stringResourceWrapper: StringResourceWrapper,
    currentLanguage: String = LanguageEnum.INDONESIAN.code,
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    useMultilineDate: Boolean = false,
    datePattern: String = MM_DD_YYYY_SLASH_PATTERN,
    oneFormatEndDate: Boolean = false,
): String {
    return try {
        // Parse start and end dates using the provided format and locale
        val dateFormat = SimpleDateFormat(datePattern, locale)
        val start = dateFormat.parse(startDate) ?: return emptyString()
        val end = dateFormat.parse(endDate) ?: return emptyString()

        // Return zero if the end date is before the start date
        if (end.before(start)) {
            val formattedEndDate = endDate.dateFormatter(
                sourcePattern = datePattern,
                targetPattern = when {
                    oneFormatEndDate || currentLanguage == LanguageEnum.INDONESIAN.code
                    -> DD_MMMM_YYYY_SPACED_PATTERN

                    else -> MMMM_DD_YYYY_PATTERN
                },
                locale = locale,
            )
            return if (useMultilineDate) {
                "${zeroString()} year\n($formattedEndDate)"
            } else {
                "${zeroString()} year ($formattedEndDate)"
            }
        }

        val startCalendar = Calendar.getInstance().apply { time = start }
        val endCalendar = Calendar.getInstance().apply { time = end }

        // Calculate the difference in years
        var years = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)

        // Check if the end date has not yet reached the start date in the current year
        if (endCalendar.get(Calendar.MONTH) < startCalendar.get(Calendar.MONTH) ||
            (
                endCalendar.get(Calendar.MONTH) == startCalendar.get(Calendar.MONTH) &&
                    endCalendar.get(Calendar.DAY_OF_MONTH) < startCalendar.get(Calendar.DAY_OF_MONTH)
                )
        ) {
            years -= 1
        }

        // If the years are 0 or negative, adjust it to 1 if the dates are in the same year
        if (years < 1 && end.after(start)) {
            years = 1
        }

        // Convert the end date back to String
        val endDateString = dateFormat.format(end)

        // Format the end date based on the provided language
        val formattedEndDate = endDateString.dateFormatter(
            sourcePattern = datePattern,
            targetPattern = when {
                oneFormatEndDate || currentLanguage == LanguageEnum.INDONESIAN.code
                -> DD_MMMM_YYYY_SPACED_PATTERN

                else -> MMMM_DD_YYYY_PATTERN
            },
            locale = locale,
        )

        // Determine the appropriate label for 'year' or 'years'
        val yearLabel =
            if (years > 1) {
                "years"
            } else {
               "year"
            }


        if (useMultilineDate) {
            "$years ${yearLabel.lowercase()}\n($formattedEndDate)"
        } else {
            "$years ${yearLabel.lowercase()} ($formattedEndDate)"
        }
    } catch (e: ParseException) {
        emptyString()
    }
}

const val MONTH_IN_A_YEAR = 12

fun String.toDate(specificPattern: String? = null): Date? {
    if (specificPattern != null) {
        try {
            val date = SimpleDateFormat(specificPattern, localeIndonesia).parse(this)
            return date
        } catch (e: ParseException) {
            Timber.e("Parse Failed: Failed for pattern $specificPattern: $e")
        }
        return null
    }

    for (pattern in supportedInputPatterns) {
        try {
            val dateFormat = SimpleDateFormat(pattern, localeIndonesia).parse(this)
            return dateFormat
        } catch (e: IllegalArgumentException) {
            Timber.e("formatDatePattern: Failed for pattern $pattern: $e")
        } catch (e: ParseException) {
            Timber.e("Parse Failed: Failed for pattern $pattern: $e")
        }
    }
    return null
}

fun String.toMillis(specificPattern: String? = null): Long? = this.toDate(specificPattern)?.time

fun getAdaptiveTimerText(millisUntilFinished: Long): String = when {
    millisUntilFinished > ONE_HOUR_MILLIS -> getHourlyTimerText(millisUntilFinished)
    millisUntilFinished > ONE_MINUTE_MILLIS -> getPerMinuteTimerText(millisUntilFinished)
    else -> getPerSecondTimerText(millisUntilFinished)
}

fun getPerSecondTimerText(millisUntilFinished: Long): String = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished).coerceAtLeast(0).toString()

fun getPerMinuteTimerText(millisUntilFinished: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished).coerceAtLeast(0)
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(minutes)).coerceAtLeast(0)

    val minuteText = if (minutes.toString().length == 1) "0$minutes" else minutes
    val secondText = if (seconds.toString().length == 1) "0$seconds" else seconds
    return "$minuteText:$secondText"
}

/**
 * Formats remaining time as a countdown (mm:ss) using ceiling rounding
 * to avoid losing seconds at the start of the timer (e.g. 60:00 → 59:59) like function `getPerMinuteTimerText`.
 * This ensures the countdown feels accurate and user-friendly.
 */
fun getCountdownTimerText(millisUntilFinished: Long): String {
    val totalSeconds = ceil(millisUntilFinished / 1000.0).toLong()

    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(minutes, seconds)
}

fun getHourlyTimerText(millisUntilFinished: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished).coerceAtLeast(0)
    val minutes = (
        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
            TimeUnit.HOURS.toMinutes(hours)
        ).coerceAtLeast(0)
    val seconds = (
        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
            (TimeUnit.MINUTES.toSeconds(minutes) + TimeUnit.HOURS.toSeconds(hours))
        ).coerceAtLeast(0)

    val hourText = if (hours.toString().length == 1) "0$hours" else hours
    val minuteText = if (minutes.toString().length == 1) "0$minutes" else minutes
    val secondText = if (seconds.toString().length == 1) "0$seconds" else seconds
    return "$hourText:$minuteText:$secondText"
}

fun getMonthYear(monthYear: String, language: String): Pair<String, String> {
    /**
     * Return the formatted month as a two-digit string and the year as a string,
     * e.g., ("12", "2025")
     **/
    try {
        val formatter = DateTimeFormatter.ofPattern(DAY_MONTH_YEAR_PATTERN, Locale(language))
        val date = LocalDate.parse("01 $monthYear", formatter)
        val month = String.format(Locale(language), "%02d", date.monthValue)
        val year = date.year.toString()
        return Pair(month, year)
    } catch (e: DateTimeParseException) {
        Timber.d("EStatement: DateTimeParseException: ${e.message}")
        return Pair("", "")
    }
}

fun minimumTransactionDate(): LocalDate {
    val millis = "1950-01-01".toMillis(
        "yyyy-MM-dd"
    )

    return Instant.ofEpochMilli(millis ?: 0L).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun getDayNameFromIndex(indexDayStr: String, currentLanguage: String): String {
    val indexDay = indexDayStr.toIntOrNull()
    if (indexDay == null || indexDay !in 1..7) return emptyString()

    val locale = when (currentLanguage.lowercase()) {
        "id", "in" -> Locale("id", "ID")
        else -> Locale("en", "US")
    }

    val symbols = DateFormatSymbols(locale)
    val weekdays = symbols.weekdays

    val orderedDays = listOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY,
    )

    val calendarDay = orderedDays[indexDay - 1]
    return weekdays[calendarDay]
}

fun addYearFromString(dateStr: String, year: Long = 1): String {
    val formatter = DateTimeFormatter.ofPattern(DD_MMMM_YYYY_SPACED_PATTERN)
    val date = LocalDate.parse(dateStr, formatter)
    val nextYearDate = date.plusYears(year)
    return nextYearDate.format(formatter)
}

fun isWorkingTimePassed(): Boolean {
    val currentTime = LocalTime.now(ZoneId.of("Asia/Jakarta")) // WIB timezone
    val currentDay = LocalDate.now(ZoneId.of("Asia/Jakarta")).dayOfWeek

    val startTime = LocalTime.of(6, 0) // 06:00 WIB
    val endTime = LocalTime.of(15, 30) // 15:30 WIB

    return when (currentDay) {
        DayOfWeek.SUNDAY, DayOfWeek.SATURDAY -> true
        in DayOfWeek.MONDAY..DayOfWeek.FRIDAY -> currentTime.isBefore(startTime) || currentTime.isAfter(endTime)
        else -> false
    }
}

/**
 * Convert millis to yyyy-MM-dd HH:mm:ss.SSSSSS z
 * Example: 1748615796144 to 2025-05-30 14:36:36.144000 UTC
 **/
fun formatToCustomUtc(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS z")
        .withZone(ZoneId.of("UTC"))

    val baseTime = formatter.format(instant)

    return baseTime
}
