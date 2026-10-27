package com.nbs.banking.core.utils

import com.nbs.banking.core.extensions.emptyString
import com.nbs.banking.core.model.LanguageEnum
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.util.Currency
import java.util.Locale
import kotlin.math.roundToInt
import timber.log.Timber

val localeIndonesia = Locale("id", "ID")
val localeEnglish = Locale("en", "EN")
const val CURRENCY_INDONESIA = "IDR"

fun Double.toStringCurrency(locale: Locale = localeIndonesia): String {
    val symbol = Currency.getInstance(locale).symbol
    val numberFormatter = NumberFormat.getNumberInstance(locale)
    val currencyNumber = numberFormatter.format(this)
    return "$symbol$currencyNumber"
}

fun LanguageEnum.getLocaleLanguage(): Locale = if (this == LanguageEnum.ENGLISH) {
    localeEnglish
} else {
    localeIndonesia
}

fun Double.toStringNumber(
    locale: Locale = localeIndonesia,
    minFractionDigit: Int = 0,
    maxFractionDigit: Int = 3,
): String {
    val formatter = NumberFormat.getNumberInstance(locale)
    formatter.apply {
        minimumFractionDigits = minFractionDigit
        maximumFractionDigits = maxFractionDigit
    }
    return formatter.format(this)
}

fun BigDecimal.toCurrencyOrRupiah(
    symbol: String = CURRENCY_INDONESIA,
    locale: Locale = localeIndonesia,
    minFractionDigit: Int = 0,
    maxFractionDigit: Int = 3,
): String = this.toDouble().toCurrencyOrRupiah(symbol, locale, minFractionDigit, maxFractionDigit)

fun Double.toCurrencyOrRupiah(
    symbol: String = CURRENCY_INDONESIA,
    locale: Locale = localeIndonesia,
    minFractionDigit: Int = 0,
    maxFractionDigit: Int = 3,
): String = if (symbol == CURRENCY_INDONESIA) {
    "Rp${this.toStringNumber(locale, minFractionDigit, maxFractionDigit)}"
} else {
    "$symbol ${this.toStringNumber(locale, minFractionDigit, maxFractionDigit)}"
}

fun Double.roundToMultipleOf(multiple: Double): Double = (this / multiple).roundToInt() * multiple

fun String.toCurrencyOrRupiah(symbol: String = CURRENCY_INDONESIA): String = if (symbol == CURRENCY_INDONESIA) {
    "Rp$this"
} else {
    "$symbol $this"
}

/**
 * @param symbol IDR , USD, etc.
 * @return String money formatted ex: Rp12.345,67.
 */
fun BigDecimal.toMoneyFormat(symbol: String = CURRENCY_INDONESIA): String {
    val prefix = if (symbol == CURRENCY_INDONESIA) {
        "Rp"
    } else {
        "$symbol "
    }

    val newDecimal = this.setScale(2, RoundingMode.HALF_EVEN).toPlainString()
    val isNegative = newDecimal.startsWith("-")
    val numeric = if (isNegative) newDecimal.substring(1) else newDecimal

    val integerPart = numeric.split(".")[0]
    val decimalPart = numeric.split(".")[1]

    val fixedIntegerPart = integerPart
        .reversed()
        .mapIndexed { index, c -> if (index % 3 == 0 && index != 0) "$c." else "$c" }
        .reversed()
        .joinToString("")

    return if (isNegative) {
        "$prefix-$fixedIntegerPart,$decimalPart"
    } else {
        "$prefix$fixedIntegerPart,$decimalPart"
    }
}

fun String.parseCurrencyToDouble(symbol: String = emptyString(), locale: Locale = localeIndonesia): Double {
    val formatter = DecimalFormat(
        "#,###.##",
        DecimalFormatSymbols(locale).apply {
            decimalSeparator = if (locale.language == "in" || locale.language == "id") ',' else '.'
        },
    )
    return try {
        formatter.parse(this.replace("$symbol ", ""))?.toDouble() ?: 0.0
    } catch (e: NullPointerException) {
        Timber.e(e)
        0.0
    } catch (e: IllegalArgumentException) {
        Timber.e(e)
        0.0
    } catch (e: ParseException) {
        0.0
    }
}

fun String.parseCurrencyToBigDecimal(symbol: String = emptyString(), locale: Locale = localeIndonesia): BigDecimal = try {
    BigDecimal(this.parseCurrencyToDouble(symbol, locale))
} catch (e: NullPointerException) {
    Timber.e(e)
    BigDecimal.ZERO
} catch (e: IllegalArgumentException) {
    Timber.e(e)
    BigDecimal.ZERO
} catch (e: ParseException) {
    Timber.e(e)
    BigDecimal.ZERO
}
