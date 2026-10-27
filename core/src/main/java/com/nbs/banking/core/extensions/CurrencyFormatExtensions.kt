package com.nbs.banking.core.extensions

import com.nbs.banking.core.model.LanguageEnum
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

fun Int.asWholeCurrency(
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
) = asCurrency(
    maxFraction = 0,
    minFraction = 0,
    locale = locale,
    customizeCurrencySymbol = customizeCurrencySymbol,
    hasCurrencySymbol = hasCurrencySymbol,
)

fun Double.asCurrency(
    maxFraction: Int = 2,
    minFraction: Int = 2,
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
    shouldRound: Boolean = true,
): String {
    val localeId = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country)
    val formatterByLocale = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    val formatterId = NumberFormat.getCurrencyInstance(localeId) as DecimalFormat

    val symbolsByLocale = formatterByLocale.decimalFormatSymbols
    val currencySymbol = when {
        customizeCurrencySymbol.isNotEmpty() -> customizeCurrencySymbol
        else -> if (locale.country == LanguageEnum.INDONESIAN.country) formatterId.decimalFormatSymbols.currencySymbol else symbolsByLocale.currencySymbol
    }

    if (hasCurrencySymbol) {
        symbolsByLocale.currencySymbol =
            if (locale.country == LanguageEnum.INDONESIAN.country) currencySymbol else currencySymbol
    } else {
        symbolsByLocale.currencySymbol = emptyString()
    }

    formatterByLocale.apply {
        decimalFormatSymbols = symbolsByLocale
        maximumFractionDigits = maxFraction
        minimumFractionDigits = minFraction
    }

    val valueToFormat = if (shouldRound) this.roundToInt().toDouble() else this
    return formatterByLocale.format(valueToFormat)
}

fun BigDecimal.asWholeCurrency(
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
    shouldRound: Boolean = true,
) = asCurrency(
    maxFraction = 0,
    minFraction = 0,
    locale = locale,
    customizeCurrencySymbol = customizeCurrencySymbol,
    hasCurrencySymbol = hasCurrencySymbol,
    shouldRound = shouldRound,
)

fun BigDecimal.asCurrency(
    maxFraction: Int = 2,
    minFraction: Int = 2,
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
    shouldRound: Boolean = true,
): String {
    val localeId = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country)
    val formatterByLocale = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    val formatterId = NumberFormat.getCurrencyInstance(localeId) as DecimalFormat

    val symbolsByLocale = formatterByLocale.decimalFormatSymbols
    val currencySymbol = when {
        customizeCurrencySymbol.isNotEmpty() -> customizeCurrencySymbol
        else -> if (locale.country == LanguageEnum.INDONESIAN.country)
            formatterId.decimalFormatSymbols.currencySymbol
        else
            symbolsByLocale.currencySymbol
    }

    if (hasCurrencySymbol) {
        symbolsByLocale.currencySymbol = currencySymbol
    } else {
        symbolsByLocale.currencySymbol = emptyString()
    }

    formatterByLocale.apply {
        decimalFormatSymbols = symbolsByLocale
        maximumFractionDigits = maxFraction
        minimumFractionDigits = minFraction
    }

    val valueToFormat =
        if (shouldRound) this.setScale(maxFraction, RoundingMode.HALF_EVEN) else this
    return formatterByLocale.format(valueToFormat)
}

fun String.asWholeCurrency(
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
    shouldRound: Boolean = true,
) = asCurrency(
    maxFraction = 0,
    minFraction = 0,
    locale = locale,
    customizeCurrencySymbol = customizeCurrencySymbol,
    hasCurrencySymbol = hasCurrencySymbol,
    shouldRound = shouldRound,
)

fun String.asCurrency(
    maxFraction: Int = 2,
    minFraction: Int = 2,
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
    shouldRound: Boolean = true,
): String {
    val number = this.toDoubleOrNull() ?: return emptyString()
    val localeId = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country)
    val formatterByLocale = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    val formatterId = NumberFormat.getCurrencyInstance(localeId) as DecimalFormat

    val symbolsByLocale = formatterByLocale.decimalFormatSymbols
    val currencySymbol = when {
        customizeCurrencySymbol.isNotEmpty() -> customizeCurrencySymbol
        else -> if (locale.country == LanguageEnum.INDONESIAN.country) formatterId.decimalFormatSymbols.currencySymbol else symbolsByLocale.currencySymbol
    }

    if (hasCurrencySymbol) {
        symbolsByLocale.currencySymbol =
            if (locale.country == LanguageEnum.INDONESIAN.country) currencySymbol else currencySymbol
    } else {
        symbolsByLocale.currencySymbol = emptyString()
    }

    formatterByLocale.apply {
        decimalFormatSymbols = symbolsByLocale
        maximumFractionDigits = maxFraction
        minimumFractionDigits = minFraction
    }

    val valueFormat = if (shouldRound) number else this
    return formatterByLocale.format(valueFormat)
}

fun Int.asCurrency(
    maxFraction: Int = 2,
    minFraction: Int = 2,
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
    customizeCurrencySymbol: String = emptyString(),
    hasCurrencySymbol: Boolean = true,
): String {
    val localeId = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country)
    val formatterByLocale = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    val formatterId = NumberFormat.getCurrencyInstance(localeId) as DecimalFormat

    val symbolsByLocale = formatterByLocale.decimalFormatSymbols
    val currencySymbol = when {
        customizeCurrencySymbol.isNotEmpty() -> customizeCurrencySymbol
        else -> if (locale.country == LanguageEnum.INDONESIAN.country) formatterId.decimalFormatSymbols.currencySymbol else symbolsByLocale.currencySymbol
    }

    if (hasCurrencySymbol) {
        symbolsByLocale.currencySymbol =
            if (locale.country == LanguageEnum.INDONESIAN.country) currencySymbol else currencySymbol
    } else {
        symbolsByLocale.currencySymbol = emptyString()
    }

    formatterByLocale.apply {
        decimalFormatSymbols = symbolsByLocale
        maximumFractionDigits = maxFraction
        minimumFractionDigits = minFraction
    }

    return formatterByLocale.format(this)
}

fun String.formatMinusCurrency(): String = if (this.startsWith("-Rp")) {
    "Rp-" + this.substring(3)
} else {
    this
}

fun String.formatCurrency(
    minFraction: Int = 2,
    maxFraction: Int = 2,
    locale: Locale = Locale(LanguageEnum.INDONESIAN.code, LanguageEnum.INDONESIAN.country),
): String {
    /*  Sample value
        value : "IDR 21.2" -> "IDR 21,20"
        value : "IDR 1000000"" -> "IDR 1.000.000,00"
        value : "IDR 1,000.75" -> "IDR 1.000,75"
     */

    val parts = this.split(" ", limit = 2)
    val (currency, rawAmount) = if (parts.size == 2) {
        parts[0] to parts[1]
    } else {
        "" to this
    }

    val normalizedAmount = when {
        rawAmount.contains(",") && rawAmount.contains(".") -> {
            rawAmount.replace(".", "").replace(",", ".")
        }

        rawAmount.contains(",") -> {
            rawAmount.replace(",", ".")
        }

        else -> {
            rawAmount
        }
    }

    val amountNumber = normalizedAmount.toDoubleOrNull() ?: return this

    val symbols = DecimalFormatSymbols(locale).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    val formatter = DecimalFormat("#,##0", symbols).apply {
        minimumFractionDigits = minFraction
        maximumFractionDigits = maxFraction
    }

    val formattedAmount = formatter.format(amountNumber)

    return if (currency.isNotEmpty()) "$currency $formattedAmount" else formattedAmount
}

fun String.formatToGroupedString(): String {
    if (this.length < 4) return this
    return this
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}
