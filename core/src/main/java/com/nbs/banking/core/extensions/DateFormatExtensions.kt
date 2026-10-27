package com.nbs.banking.core.extensions

import java.text.SimpleDateFormat
import java.util.Locale
import timber.log.Timber

/**
 * Formats a date string from one pattern to another.
 *
 * @param sourcePattern The pattern of the source date string. Default is "yyyy-MM-dd".
 * @param targetPattern The pattern of the target formatted date string. Default is "dd-MM-yyyy".
 * @param locale The locale used for parsing the source date string. Default is "in_ID" (Indonesian).
 * @return The formatted date string if parsing is successful, otherwise returns the original string.
 */

fun String.dateFormatter(
    sourcePattern: String = YYYY_MM_DD_DASH_PATTERN,
    targetPattern: String = DD_MM_YY_DASH_PATTERN,
    locale: Locale = Locale.getDefault(),
): String = runCatching {
    val date = SimpleDateFormat(sourcePattern, locale).parse(this)
    SimpleDateFormat(targetPattern, locale).format(requireNotNull(date))
}.onFailure {
    Timber.e(it, "Error formatting date string: $this")
}.onSuccess {
    Timber.d("Formatted date string success: $it with locale: ${locale.language}")
}.getOrDefault(this)

const val DATE_TIME_TIMEZONE_PATTERN: String = "yyyy-MM-dd'T'HH:mm:ss.SSS"
const val YYYY_MM_DD_HH_PATTERN: String = "yyyy-MM-dd'T'HH:mm"
const val DATE_TIME_TO_DISPLAY_PATTERN: String = "dd MMMM yyyy, HH:mm"
const val YYYY_MM_DD_PATTERN = "yyyyMMdd"
const val DD_MMMM_YYYY_SPACED_PATTERN = "dd MMMM yyyy"
const val D_MMMM_YYYY_SPACED_PATTERN = "d MMMM yyyy"
const val MMMM_DD_YYYY_PATTERN = "MMMM dd yyyy"
const val DD_MM_YYYY_DASH_PATTERN = "dd-MM-yyyy"
const val DD_MM_YY_DASH_PATTERN = "dd-MM-yyyy"
const val DD_MM_YYYY = "ddMMyy"
const val YYYY_MM_DD_DASH_PATTERN = "yyyy-MM-dd"
const val MM_DD_YYYY_SLASH_PATTERN = "MM/dd/yyyy"
const val MMMM_YYYY_SPACED_PATTERN = "MMMM YYYY"
const val DATE_TIME_DASH_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS"
const val YYYY_MM_DD_HH_MM_SS_COMPACT_PATTERN = "yyyyMMddHHmmss"
const val MM_SLASH_YY_PATTERN = "MM/yy"
const val YYYY_DASH_MM_PATTERN = "yyyy-MM"
const val DD_MMM_YYYY_HH_MM_PATTERN = "dd MMM yyyy, HH:mm"
const val DD_MMMM_YYYY_HH_MM_PATTERN: String = "dd MMMM yyyy HH:mm"
const val DD_MMM_YYYY_BAR_HH_MM_PATTERN: String = "dd MMM yyyy | HH:mm"
const val D_MMMM_PATTERN: String = "d MMMM"
const val DD_MMM_YYYY_SPACED_PATTERN = "dd MMM yyyy"
