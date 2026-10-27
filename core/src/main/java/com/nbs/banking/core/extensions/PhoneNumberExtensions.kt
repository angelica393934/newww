package com.nbs.banking.core.extensions

import com.nbs.banking.core.extensions.PhoneNumberExtensions.PHONE_NUMBER_SEPARATOR_STRING
import com.nbs.banking.core.extensions.PhoneNumberExtensions.PHONE_PREFIX_ID
import com.nbs.banking.core.extensions.PhoneNumberExtensions.PHONE_PREFIX_ID_LOCAL
import com.nbs.banking.core.extensions.PhoneNumberExtensions.PHONE_PREFIX_ID_WITHOUT_PLUS
import com.nbs.banking.core.extensions.PhoneNumberExtensions.PLUS_PREFIX
import com.nbs.banking.core.extensions.PhoneNumberExtensions.ZERO_PREFIX
import timber.log.Timber

object PhoneNumberExtensions {
    const val PHONE_PREFIX_ID = "+62"
    const val PHONE_PREFIX_ID_WITHOUT_PLUS = "62"
    const val PHONE_PREFIX_ID_LOCAL = "08"
    const val PHONE_MAX_LENGTH = 16
    const val ZERO_PREFIX = "0"
    const val PLUS_PREFIX = "+"
    const val PHONE_NUMBER_SEPARATOR_CHAR = '-'
    const val PHONE_NUMBER_SEPARATOR_STRING = "-"
}

/**
 * Formats a local phone number into an international format.
 *
 * @param phoneNumber The local phone number to format.
 * @param countryCode The country code, with or without a '+' prefix (e.g., "+62" or "62").
 * @param separator The separator to use between digit groups (default is a space).
 * @param isShowPlusPrefix If true, includes a '+' prefix in the output (default is false).
 *
 * @return A formatted international phone number.
 *
 * @sample
 * formatInternationalPhoneNumber("08123456789", "+62") // returns "62 812 3456 789"
 * formatInternationalPhoneNumber("08123456789", "+62", "-", true) // returns "+62-812-3456-789"
 */
fun formatInternationalPhoneNumber(
    phoneNumber: String,
    countryCode: String,
    separator: String = " ",
    isShowPlusPrefix: Boolean = false,
): String {
    val code = countryCode.removePrefix("+")
    val formattedPhoneNumber = revertInternationalPhoneNumber(
        phoneNumber = phoneNumber,
        countryCode = countryCode,
    )

    val fullPhoneNumber = if (code == PHONE_PREFIX_ID_WITHOUT_PLUS) {
        formattedPhoneNumber.removePrefix("0")
    } else {
        formattedPhoneNumber
    }

    val groups = mutableListOf<String>()
    var iteration = 0

    while (iteration < fullPhoneNumber.length) {
        val endIndex = if (iteration.isZero()) {
            (iteration + 3).coerceAtMost(fullPhoneNumber.length)
        } else {
            (iteration + 4).coerceAtMost(fullPhoneNumber.length)
        }

        groups.add(fullPhoneNumber.substring(iteration, endIndex))
        iteration = endIndex
    }

    val phoneNumberWithoutCountryCode = groups.joinToString(separator = separator)
    val result = "$code $phoneNumberWithoutCountryCode"

    return if (isShowPlusPrefix) "+$result" else result
}

/**
 * Converts an international phone number to a local format.
 *
 * @param phoneNumber The phone number with international format.
 * @param countryCode The country code (with or without '+').
 * @return The local-format phone number.
 *
 * @sample
 * revertInternationalPhoneNumber("+628123456789", "+62") // returns "08123456789"
 *
 * @sample
 * revertInternationalPhoneNumber("628123456789", "62") // returns "08123456789"
 *
 * @sample
 * revertInternationalPhoneNumber("+12025550123", "+1") // returns "2025550123"
 */
fun revertInternationalPhoneNumber(phoneNumber: String, countryCode: String): String {
    val countryCodeWithoutPlusPrefix = countryCode.removePrefix("+")

    val phoneNumberWithoutAnyPrefix = phoneNumber.removePrefix("+").removePrefix(countryCodeWithoutPlusPrefix)
    val phoneNumberWithoutAnyFormat = phoneNumberWithoutAnyPrefix.removeSpaces().filter { it.isDigit() }

    if (countryCodeWithoutPlusPrefix == PHONE_PREFIX_ID_WITHOUT_PLUS) {
        val phoneNumberWithoutZeroPrefix = phoneNumberWithoutAnyFormat.removePrefix("0")
        return "0$phoneNumberWithoutZeroPrefix"
    }

    return phoneNumberWithoutAnyFormat
}

fun String.normalizedPhonePrefix(prefixes: List<String> = listOf(ZERO_PREFIX, PHONE_PREFIX_ID_WITHOUT_PLUS, PHONE_PREFIX_ID)): String {
    val filterInput = this.filter { it.isPhoneNumber() }
    Timber.i("filterInput: $filterInput")
    val cleanedInput = filterInput.replace(PLUS_PREFIX, emptyString()).replace(
        space(),
        emptyString()
    ).replace(PHONE_NUMBER_SEPARATOR_STRING, emptyString())
    Timber.i("cleanedInput: $cleanedInput")
    return when {
        cleanedInput.startsWith(prefixes[0]) -> cleanedInput.drop(1)
        cleanedInput.startsWith(prefixes[1]) -> cleanedInput.drop(2)
        cleanedInput.startsWith(prefixes[2]) -> cleanedInput.drop(3)
        else -> cleanedInput
    }
}

 fun String.asPhoneNumber(normalizedFromPrefix: () -> String = { normalizedPhonePrefix() }, newPrefix: String = PHONE_PREFIX_ID): String {
    // Filter number if phoneNumber > 14 (max length without prefix)
    val finalInput = if (normalizedFromPrefix().length > 14) {
        normalizedFromPrefix().dropLast(normalizedFromPrefix().length - 14)
    } else {
        normalizedFromPrefix()
    }

    val appendNewPrefix = newPrefix.plus(" ")

    // Format the number as XXX-XXXX-XXXX-XXXX-XXX
    val formattedNumber = buildString {
        append(appendNewPrefix)
        for (i in finalInput.indices) {
            append(finalInput[i])
            val isCurrentNumberCountPassToAppend = i == 2 || i == 6 || i == 10 || i == 14
            if (isCurrentNumberCountPassToAppend && i != finalInput.lastIndex) {
                append('-')
            }
        }
    }

    return formattedNumber
 }

fun String.asPhoneNumber(
    normalizedFromPrefix: () -> String = { normalizedPhonePrefix() },
    newPrefix: String = PHONE_PREFIX_ID,
    prefixSeparator: String = " ",
    groupSeparator: String = "-",
): String {
    val normalized = normalizedFromPrefix().filter { it.isDigit() }

    val isIndonesian = this.startsWith(PHONE_PREFIX_ID) ||
        this.startsWith(PHONE_PREFIX_ID_WITHOUT_PLUS) ||
        this.startsWith(ZERO_PREFIX)

    val finalInput = if (normalized.length > 14) normalized.take(14) else normalized

    return if (!isIndonesian) {
        val countryCodeLength = when {
            this.startsWith("+") -> this.drop(1).take(3).takeWhile { it.isDigit() }.length
            else -> 1
        }
        val countryCode = this.removePrefix("+").take(countryCodeLength).ifEmpty { "1" }
        val numberPart = this.removePrefix("+").removePrefix(countryCode).filter { it.isDigit() }
        val groups = mutableListOf<String>()
        var i = 0

        while (i < numberPart.length) {
            val groupLength = when (i) {
                0 -> 3
                3 -> 3
                else -> 4
            }
            val end = (i + groupLength).coerceAtMost(numberPart.length)
            groups.add(numberPart.substring(i, end))
            i = end
        }

        "+$countryCode$prefixSeparator" + groups.joinToString("-")
    } else {
        val appendNewPrefix = newPrefix.plus(prefixSeparator)

        buildString {
            append(appendNewPrefix)
            for (i in finalInput.indices) {
                append(finalInput[i])
                val isCurrentNumberCountPassToAppend = i == 2 || i == 6 || i == 10 || i == 14
                if (isCurrentNumberCountPassToAppend && i != finalInput.lastIndex) {
                    append(groupSeparator)
                }
            }
        }
    }
}

fun String.formatPhoneNumberDash(): String {
    val digits = this.filter { it.isDigit() }
    if (digits.length < 9) return digits

    val withZeroPrefix = when {
        digits.startsWith("0") -> digits
        digits.startsWith("8") -> "0$digits"
        digits.startsWith("2") -> "0$digits"
        else -> digits
    }

    // Landline: 021-XXXX-REST
    if (withZeroPrefix.startsWith("02")) {
        return buildString {
            append(withZeroPrefix.substring(0, 3))
            append("-")
            append(withZeroPrefix.substring(3, 7))
            if (withZeroPrefix.length > 7) {
                append("-")
                append(withZeroPrefix.substring(7))
            }
        }
    }

    // Mobile: 08XX-XXXX-REST
    return buildString {
        append(withZeroPrefix.substring(0, 4))
        append("-")
        append(withZeroPrefix.substring(4, 8))
        if (withZeroPrefix.length > 8) {
            append("-")
            append(withZeroPrefix.substring(8))
        }
    }
}

fun String.revertPhoneNumber(normalizedFromPrefix: () -> String = { this.normalizedPhonePrefix() }, addZeroPrefix: Boolean = true): String {
    // Remove any dashes or spaces from prefix
    val cleanedNumber = normalizedFromPrefix()

    Timber.i("cleanedNumber: $cleanedNumber")
    // Add the leading 0
    return if (addZeroPrefix) ZERO_PREFIX + cleanedNumber else cleanedNumber
}

fun isInternationalNumber(phoneNumber: String): Boolean = !(
    phoneNumber.startsWith(PHONE_PREFIX_ID) ||
        phoneNumber.startsWith(PHONE_PREFIX_ID_LOCAL) ||
        phoneNumber.startsWith(PHONE_PREFIX_ID_WITHOUT_PLUS)
    )
