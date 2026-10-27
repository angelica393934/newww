package com.nbs.banking.core.extensions

import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nbs.banking.core.utils.localeIndonesia
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.roundToInt

fun Int?.orZero(): Int = this ?: 0

fun BigInteger?.orZero(): BigInteger = this ?: BigInteger.ZERO

fun Float?.orZero(): Float = this ?: 0f

fun Double?.orZero(): Double = this ?: 0.0

fun Long?.orZero(): Long = this ?: 0L

fun Double.isZero(): Boolean = this == 0.0

fun Float.isZero(): Boolean = this == 0f

fun Int.isZero(): Boolean = this == 0

fun Long.isZero(): Boolean = this == 0L

fun Double.isNotZero(): Boolean = this != 0.0

fun Float.isNotZero(): Boolean = this != 0f

fun Int.isNotZero(): Boolean = this != 0

fun Long.isNotZero(): Boolean = this != 0L

fun Int?.isNullOrZero(): Boolean = this == null || this == 0

fun Double?.isNullOrZero(): Boolean = this == null || this == 0.0

fun Double?.isNotNullOrZero(): Boolean = this != null && this != 0.0

fun BigDecimal?.isNotNullOrZero(): Boolean = this != null && this != BigDecimal.ZERO

fun BigDecimal?.isNullOrZero(): Boolean = this == null || this == BigDecimal.ZERO

fun BigDecimal?.isMoreThanZero(): Boolean = this?.let { it > BigDecimal.ZERO } == true

fun Double?.toPercentOrZero(): Double = this?.div(100) ?: 0.0

fun Int.toDp(): Dp = (this.toFloat() / Resources.getSystem().displayMetrics.density).dp

fun BigDecimal?.orZero(): BigDecimal = this ?: BigDecimal.ZERO

fun BigDecimal?.orNullString(): String? = this?.toString()

fun BigDecimal?.toStringOrEmpty(): String = if (this == BigDecimal.ZERO) emptyString() else this.toString()

fun BigDecimal?.orZeroString(): String = this?.toString() ?: BigDecimal.ZERO.toString()

fun BigDecimal.roundUpTo(multiple: BigDecimal): BigDecimal {
    val roundedValue = this.divide(multiple, 0, RoundingMode.CEILING).multiply(multiple)

    return roundedValue.max(multiple)
}

fun BigDecimal.roundDownTo(multiple: BigDecimal): BigDecimal {
    val roundedValue = this.divide(multiple, 0, RoundingMode.FLOOR).multiply(multiple)

    return roundedValue.max(multiple)
}

fun Double?.formatToStringWithTwoDecimals(): String = this?.let {
    val formatter = NumberFormat.getInstance(localeIndonesia) as DecimalFormat
    formatter.applyPattern("##0.00")
    if (it == 0.0) {
        "0"
    } else {
        formatter.format(it)
    }
} ?: emptyString()

fun BigDecimal?.formatToStringWithTwoDecimals(): String = this?.let {
    val formatter = NumberFormat.getInstance(localeIndonesia) as DecimalFormat
    formatter.applyPattern("##0.00")
    if (it == BigDecimal(0)) {
        "0"
    } else {
        formatter.format(it)
    }
} ?: emptyString()

fun Double?.rountToIntString() = this?.roundToInt().toString()

fun Any?.toBigDecimal(): BigDecimal? = when (this) {
    is BigDecimal -> this
    is Int -> BigDecimal(this)
    is Double -> BigDecimal.valueOf(this)
    is String -> this.toBigDecimalOrNull()
    else -> null
}

fun String?.convertDecimalSymbolToPoint() = this?.replace(",", ".")

fun String?.convertDecimalSymbolToComma() = this?.replace(".", ",")

fun String?.normalizeThousandAndDecimalSeparators(): String? {
    if (this == null) return null

    val cleaned = this.replace(".", emptyString())
    return cleaned.replace(",", ".")
}

fun Any?.toIntOrZero() = this?.toString()?.toIntOrNull() ?: zeroInt()

fun String?.clearDecimalSymbol(): String? = this?.split('.', ',')?.firstOrNull()

fun String.formatToTwoDecimals(): String {
    val trimmed = this.trim()

    val number = trimmed.replace(",", ".").toDoubleOrNull()

    return if (number != null) {
        String.format(locale = localeIndonesia, "%.2f", number).replace(".", ",")
    } else {
        "0,00"
    }
}

fun Int?.asStringOrNull(context: Context): String? = this?.takeIf { it != 0 }?.let { context.getString(it) }

fun String?.removeDecimalPointToInt() = this?.replace(".", "").toIntOrZero()

fun Long?.formatToThousandWithDotOrZeroString(): String = this?.let {
    val formatter = NumberFormat.getInstance(localeIndonesia) as DecimalFormat
    formatter.applyPattern("#,###")
    formatter.format(it)
} ?: zeroString()

fun String?.removeDecimalPointToLong() = this?.replace(".", "")?.toLong() ?: 0L

fun BigDecimal.isGreaterThan(other: BigDecimal): Boolean = this > other

fun String?.replaceDotToComma(): String = this?.replace(".", ",").orEmpty()

fun String?.replaceCommaToDot(): String = this?.replace(",", ".").orEmpty()
