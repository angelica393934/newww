package com.nbs.banking.core.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat
import androidx.core.text.isDigitsOnly
import com.nbs.banking.core.extensions.StringExtension.EMPTY_DASH
import com.nbs.banking.core.extensions.StringExtension.EMPTY_SPACE
import com.nbs.banking.core.extensions.StringExtension.EMPTY_STRING
import com.nbs.banking.core.extensions.StringExtension.EMPTY_UNDERSCORE
import com.nbs.banking.core.extensions.StringExtension.ZERO_CHAR
import com.nbs.banking.core.extensions.StringExtension.ZERO_DECIMAL
import com.nbs.banking.core.extensions.StringExtension.ZERO_INT
import com.nbs.banking.core.extensions.StringExtension.ZERO_LONG
import com.nbs.banking.core.extensions.StringExtension.ZERO_PERCENT_STRING
import com.nbs.banking.core.extensions.StringExtension.ZERO_STRING
import com.nbs.banking.core.model.LanguageEnum
import com.nbs.banking.core.utils.Constants.AMEX_BIN_NUMBER_THIRTY_FOUR
import com.nbs.banking.core.utils.Constants.AMEX_BIN_NUMBER_THIRTY_SEVEN
import com.nbs.banking.core.utils.Constants.AMEX_MAX_NUMBER
import com.nbs.banking.core.utils.Constants.HTTPS_PROTOCOL
import com.nbs.banking.core.utils.Constants.HTTP_PROTOCOL
import com.nbs.banking.core.utils.Constants.MASKING_MAX_RANGE
import com.nbs.banking.core.utils.Constants.MASKING_MIN_RANGE
import com.nbs.banking.core.utils.Constants.VIMAS_MAX_NUMBER
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.UUID

val emojiRegex = Regex("[\uD83C-\uDBFF\uDC00-\uDFFF]+")
val phoneIdRegex = Regex("^(\\+62|62|0)?[ -]?(8[0-9]*)[ -]?([0-9]{1,4}[ -]?){0,3}\$")
val nickNameRegex = Regex("^[a-zA-Z0-9][a-zA-Z0-9\\s_-]*(?<!\\s{2})[a-zA-Z0-9]*$")
val fullNameRegex = Regex("^[A-Za-z0-9 .-]*$")

object StringExtension {
    const val EMPTY_STRING = ""
    const val EMPTY_DASH = "-"
    const val ZERO_STRING = "0"
    const val ZERO_CHAR = '0'
    const val ZERO_INT = 0
    const val ZERO_LONG = 0L
    const val ZERO_DECIMAL = 0.0
    const val ZERO_PERCENT_STRING = "0%"
    const val SLASH = "/"
    const val EMPTY_UNDERSCORE = "_"
    const val EMPTY_SPACE = " "
}

fun String.removeSpaces(): String = this.replace("\\s".toRegex(), "")

fun String?.orDash(): String = ifNullOrEmpty("-")

fun String?.orZeroString(): String = ifNullOrEmpty("0")

fun String?.orZeroCurrency(): String = ifNullOrEmpty("0".asCurrency())

fun String?.orZeroInt(): Int = ifNullOrEmpty("0").toInt()

/**
 * Fallback for the `firebaseToken` request field, which the backend expects to be non-empty.
 *
 * Applied where the request body is populated, never in storage: []
 * returns the real stored token so callers can detect when the FCM token has actually changed.
 */
fun String?.orDummyFirebaseToken(): String = ifNullOrEmpty { "${UUID.randomUUID()}-DmyX" }

fun String?.isZeroNullOrEmpty(): Boolean = this == "0" || this == "0.0" || this.isNullOrEmpty()

fun String?.isZeroOrIdPhoneNumber(): Boolean {
    val isPhoneNumberStartWithZero = this == "0"
    val isPhoneNumberStartWith08 = this == "08"
    val isPhoneNumberStartWithPlus62 = this == "+62"
    val isPhoneNumberStartWith62 = this == "62"
    val isPhoneNumberStartWith6 = this == "6"
    val isPhoneNumberStartWithPlus6 = this == "+6"
    val isPhoneNumberStartWithPlus = this == "+"
    val isPhoneNumberStartWithPlus62Space = this == "+62 "

    return isPhoneNumberStartWithZero ||
            isPhoneNumberStartWith08 ||
            isPhoneNumberStartWithPlus62 ||
            isPhoneNumberStartWith62 ||
            isPhoneNumberStartWith6 ||
            isPhoneNumberStartWithPlus6 ||
            isPhoneNumberStartWithPlus ||
            isPhoneNumberStartWithPlus62Space
}

fun String?.isNotZeroNullOrEmpty(): Boolean = !this.isZeroNullOrEmpty()

fun String?.isNotEmptyOrDash(): Boolean = !this.isNullOrEmpty() && this.isNotEmpty() && this != "-"

fun String?.isNotEmptyOrZero(): Boolean = !this.isNullOrEmpty() && this.isNotEmpty() && this != "0"

fun String?.ifNullOrEmpty(defaultValue: String): String =
    if (this.isNullOrEmpty()) defaultValue else this

fun String?.ifNullOrEmpty(defaultValue: () -> String): String =
    if (this.isNullOrEmpty()) defaultValue() else this

fun String?.ifZeroNullOrEmpty(defaultValue: () -> String): String =
    if (this.isZeroNullOrEmpty()) defaultValue() else this.orEmpty()

fun Any?.toStringNullOrEmpty(): String =
    if (this.toString().equals("null", true)) emptyString() else this.toString()

fun String?.getImageTitleAfterSlash(): String = this.orEmpty().substringAfterLast("/")

fun String?.getTitleAfterSpace(): String = this.orEmpty().substringAfterLast(" ")

fun emptyString() = EMPTY_STRING

fun space(amount: Int = 1) = " ".repeat(amount)

fun emptyDash() = EMPTY_DASH

fun emptyUnderScore() = EMPTY_UNDERSCORE

fun zeroString() = ZERO_STRING

fun zeroChar() = ZERO_CHAR

fun zeroInt() = ZERO_INT

fun zeroLong() = ZERO_LONG

fun zeroDecimal() = ZERO_DECIMAL

fun zeroPercentString() = ZERO_PERCENT_STRING

fun String?.toBigDecimalOrZero(
    locale: Locale = Locale(
        LanguageEnum.INDONESIAN.code,
        LanguageEnum.INDONESIAN.country,
    ),
    shouldRound: Boolean = true,
    scaleValue: Int = 2,
    roundingMode: RoundingMode = RoundingMode.HALF_EVEN,
): BigDecimal = if (this.isNullOrEmpty()) {
    BigDecimal.ZERO
} else {
    try {
        val decimalSeparator = DecimalFormatSymbols(locale).decimalSeparator
        val result = BigDecimal(this.replace(decimalSeparator, '.'))
        if (shouldRound) result.setScale(scaleValue, roundingMode) else result
    } catch (e: NumberFormatException) {
        BigDecimal.ZERO
    }
}

fun String?.toBigDecimalOrNull(
    locale: Locale = Locale(
        LanguageEnum.INDONESIAN.code,
        LanguageEnum.INDONESIAN.country,
    ),
    shouldRound: Boolean = true,
): BigDecimal? = if (this.isNullOrEmpty()) {
    null
} else {
    try {
        val decimalSeparator = DecimalFormatSymbols(locale).decimalSeparator
        val result = BigDecimal(this.replace(decimalSeparator, '.'))
        if (shouldRound) result.setScale(2, RoundingMode.HALF_EVEN) else result
    } catch (e: NumberFormatException) {
        null
    }
}

fun Double?.toBigDecimalOrZero(
    locale: Locale = Locale(
        LanguageEnum.INDONESIAN.code,
        LanguageEnum.INDONESIAN.country,
    ),
): BigDecimal = this?.toString().toBigDecimalOrZero(locale)

fun String?.hasMinus(): Boolean = this?.contains("-") == true

fun String?.cleanDash() = this?.replace("-", "").orEmpty()

fun String.maskingCardNumberAsteriskLastFour(): String {
    val lastFourDigits = this.takeLast(4)
    return "*** $lastFourDigits"
}

fun String.maskingCreditCardAsteriskCenterSixDigits(): String {
    val cleanedText = this.replace(" ", "")
    if (cleanedText.length !in AMEX_MAX_NUMBER..VIMAS_MAX_NUMBER) return this

    val isAmex = cleanedText.startsWith(AMEX_BIN_NUMBER_THIRTY_FOUR) ||
            cleanedText.startsWith(AMEX_BIN_NUMBER_THIRTY_SEVEN)
    val maskRange = MASKING_MIN_RANGE..MASKING_MAX_RANGE

    return cleanedText.mapIndexed { i, char ->
        val shouldAddSpace = when {
            isAmex -> i == 3 || i == 9
            else -> (i + 1) % 4 == 0 && i != cleanedText.lastIndex
        }
        (if (i in maskRange) '*' else char).toString() + if (shouldAddSpace) " " else ""
    }.joinToString("")
}

fun String.maskCardNumberX() = "xx${takeLast(4)}"

fun String.percent() = "$this%"

fun String?.maskingEmail(): String {
    if (this.isNullOrEmpty()) return emptyString()

    val emailParts = this.split("@")
    if (emailParts.size != 2 || !emailParts[1].contains(".")) {
        return this
    }

    val namePart = emailParts[0]
    val domainPart = emailParts[1]

    if (namePart.length < 3) return namePart + "*".repeat(4) + "@$domainPart"

    val maskedNamePart = when {
        namePart.length <= 5 -> {
            namePart.take(3) + "*".repeat(4)
        }

        else -> {
            namePart.take(3) + "*".repeat(4) + namePart.takeLast(2)
        }
    }

    return "$maskedNamePart@$domainPart"
    /* Result Example
        ang@gmail.com -> ang****@gmail.com
        ange@gmail.com -> ang****@gmail.com
        angel@gmail.com -> ang****@gmail.com
        angel1@gmail.com -> ang****@gmail.com
        angel12@gmail.com -> ang****12@gmail.com
        angel123@gmail.com -> ang****23@gmail.com
        angel1234@gmail.com -> ang****34@gmail.com
        test@yahoo.com -> tes****@yahoo.com
        short@gmail.com -> sho****@gmail.com
        a@gmail.com -> a****@gmail.com
        noah.com -> noah.com ///invalid format email
        just@dotcom -> just@gmailcom ///invalid format email
     */
}

fun String?.maskingPhoneNumber(): String {
    if (this == null) return ""

    val cleanedNumber = this.filter { it.isDigit() }
    if (cleanedNumber.isEmpty()) return ""
    return when {
        cleanedNumber.length < 10 -> {
            val lastTwoDigits = cleanedNumber.takeLast(2)
            val maskedPart = "*".repeat(cleanedNumber.length - 2)
            "$maskedPart$lastTwoDigits"
        }

        else -> {
            val lastFourDigits = cleanedNumber.takeLast(4)
            val maskedPart = "*".repeat(cleanedNumber.length - 4)
            "$maskedPart$lastFourDigits"
        }
    }
    /* Result Example
        "082144443123" = Output "********3123"
        "6282144443123 = Output "********3123"
        "0812345678" = Output: ******5678"
        "12345" = Output: ***45
     */
}

// Converts String to URL-safe format for routing in navigation bottom sheet
fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

// Reverts Encode String to original format post-routing in navigation bottom sheet
fun String.urlDecode(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.toString())

fun Char.isPhoneNumber(): Boolean = this.isDigit() || this == '+' || this == '-' || this == ' '

fun String.isPhoneNumber(): Boolean =
    this.isDigitsOnly() or this.contains("+") or this.contains("-") or this.contains(" ")

fun String.isPhoneNumberByRegex(): Boolean = this.matches(phoneIdRegex)

fun String.isPhoneNumberId(): Boolean {
    val isNotStartWithEight =
        !this.startsWith("8") // this condition for separate query data if user start searching with 8
    return (isNotStartWithEight && this.isPhoneNumberByRegex()) || this.isZeroOrIdPhoneNumber()
}

fun String.isEmoji(): Boolean = emojiRegex.containsMatchIn(this)

fun String.getInitialName(): String {
    val name = this.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }

    val firstName = name.firstOrNull()?.let {
        if (it.getFirstCharacter().isEmoji()) getFirstEmoji(name.first()) else name.first()
            .getFirstCharacter()
    }.orEmpty()
    val lastName = name.lastOrNull()?.let {
        if (it.getFirstCharacter().isEmoji()) getFirstEmoji(name.last()) else name.last()
            .getFirstCharacter()
    }.orEmpty()

    return when {
        name.size > 1 -> "${firstName.uppercase()}${lastName.uppercase()}"
        name.isNotEmpty() -> firstName.uppercase()
        else -> emptyDash()
    }
}

fun getFirstEmoji(input: String): String? {
    val emojiRegex = "\\p{So}|\\p{Cn}".toRegex() // Matches symbols and other emoji categories
    val firstEmoji = emojiRegex.find(input)
    return firstEmoji?.value
}

fun String.getFirstCharacter(): String = this.take(1)

fun String.formatCardNumberStandard(): String = this
    .replace(" ".toRegex(), "")
    .chunked(4)
    .joinToString(" ")

fun String.formatCardNumberAmex(): String = if (this.length == 15) {
    "${this.substring(0, 4)} ${this.substring(4, 10)} ${this.substring(10, 15)}"
} else {
    this
}

fun String.extractMerchantName(): String {
    val parts = this.trim().split("\\s+".toRegex())
    return parts.dropLast(1).joinToString(" ")
}

fun String.extractListFromHtml(): List<String> {
    val regex = "<li>(.*?)</li>".toRegex()
    return regex.findAll(this).map { it.groupValues[1] }.toList()
}

fun String.formatProxy(): String {
    val emailRegex = "^(([a-zA-Z0-9\\-._]+)@(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))\$".toRegex()
    return if (emailRegex.matches(this)) {
        this
    } else {
        if (this.isEmpty()) return emptyString() else this.asPhoneNumber()
    }
}

fun String.formatPhoneNumber(separator: String = " "): String {
    val formattedNumber = StringBuilder()
    var groupLength = 0
    val preFormat = if (this.startsWith("62")) {
        this.replaceFirst("62", "0")
    } else {
        this
    }

    for (char in preFormat) {
        if (char.isDigit()) {
            formattedNumber.append(char)
            groupLength++

            if (groupLength == 4) {
                formattedNumber.append(separator)
                groupLength = 0
            }
        }
    }

    if (formattedNumber.endsWith(separator)) {
        formattedNumber.deleteCharAt(formattedNumber.length - 1)
    }

    return if (formattedNumber.startsWith("0")) {
        formattedNumber.toString().replaceFirst("0", "+62 ")
    } else {
        formattedNumber.toString()
    }
}

fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)

                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)

                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                    start,
                    end,
                )
            }

            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )

            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
        }
    }
}

fun String.toHtml() = SpannableStringBuilder(this).toString().let {
    HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
}.toAnnotatedString()

/**
 * For example, "hello world" becomes "Hello World".
 */
fun String.capitalized(): String = lowercase().split(" ").joinToString(" ") { text ->
    text.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase() else char.toString()
    }
}

fun String.decapitalized() = lowercase().split(" ").joinToString(" ") { text ->
    text.replaceFirstChar { char ->
        if (char.isUpperCase()) char.lowercase() else char.toString()
    }
}

/**
 * For example, "hello world" becomes "Hello World".
 */
fun String.capitalizeWordsUpperCase(): String = split(" ").joinToString(" ") {
    it
        .lowercase()
        .replaceFirstChar(Char::uppercase)
}

fun String.isTextOnly(regex: Regex = "^[a-zA-Z ]*$".toRegex()): Boolean = this.matches(regex)

fun String.isTextNumericOnly(regex: Regex = "-?\\d+(\\.\\d+)?".toRegex()): Boolean =
    this.matches(regex)

fun String.numericOnly(): String = this.replace("[^0-9]".toRegex(), "")

fun String.htmlToString(): Spanned? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    @Suppress("DEPRECATION")
    Html.fromHtml(this)
}

fun String.smartUIMaskingAccountNumber(): String = if (this.length <= 4) {
    val maskedPart = "*".repeat(3)
    "$maskedPart $this"
} else {
    this.maskingAccountNumber()
}

fun String.maskingAccountNumber(maskedPartLength: Int = 3): String {
    if (this.length <= 4) return this // Return the original string if it's too short to mask
    val lastFourDigits = this.takeLast(4)
    val maskedPart = "*".repeat(maskedPartLength)
    return "$maskedPart $lastFourDigits"
}

fun String.base64ToBitmap(): Bitmap? {
    try {
        val byteArray = Base64.decode(this, Base64.NO_WRAP)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    } catch (e: IllegalArgumentException) {
        Timber.tag("BASE64_TO_BITMAP").e("$e")
        return null
    }
}

fun String.capitalizeFirstWord() = lowercase()
    .split(space())
    .mapIndexed { index, word ->
        if (index.isZero()) {
            word.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        } else {
            word
        }
    }.joinToString(space())

fun String.extractRegistryKey(prefix: String = "#_"): String = if (this.startsWith(
        prefix,
    )
) {
    this.removePrefix(prefix)
} else {
    this
}

//@Composable
//fun String.suffixMonth(isLanguageEn: Boolean): String {
//    if (isEmpty()) return emptyString()
//    val text = stringResource(
//        R.string.common_label_format_month,
//        this,
//    )
//    if (!isLanguageEn) return text
//    return if (this == "1") {
//        text
//    } else {
//        text + "s"
//    }
//}
//
///**
// * If the value of String is 1 -> 1 Month.
// * If the value of String more than 1 -> 2 Months, 3 Months, etc.
// * */
//fun String.shortMonth(isLanguageEn: Boolean, context: Context?): String {
//    if (isEmpty()) return emptyString()
//    val text = context?.getString(
//        R.string.common_label_format_month,
//        this,
//    )
//    if (!isLanguageEn) return text.orEmpty()
//    return if (this == "1") {
//        text.orEmpty()
//    } else {
//        text + "s"
//    }
//}

fun String.camelToSnakeCase(): String = this.replace(Regex("([a-z])([A-Z]+)"), "$1_$2").lowercase()

fun String.snakeToCapitalize(): String = this.split('_').joinToString(" ") { it.capitalized() }

fun String.snakeToPascalCase(): String =
    this.split('_').joinToString("") { it.capitalizeWordsUpperCase() }

fun Char.isSpecialChar(): Boolean {
    val specialCharRegex = Regex("[!@#$%^&*(),.?\":{}|<>]")
    return specialCharRegex.containsMatchIn(this.toString())
}

fun String.toAlphaNumericOnly() = filter { it.isLetterOrDigit() || it.isWhitespace() }

fun String.toAlphaNumericNoSpace(): String = filter { it.isLetterOrDigit() }

fun String.toAlphaNumericDashSpaceOnly(): String =
    filter { it.isLetterOrDigit() || it.isWhitespace() || it == '-' }

fun String.toNumericOnly(): String = filter { it.isDigit() }

fun String.toAlphaSpaceOnly(): String = filter { it.isLetter() || it.isWhitespace() }

fun String.toAlphaNumericDotSpaceOnly(): String =
    filter { it.isLetterOrDigit() || it.isWhitespace() || it == '.' }

fun String.maskBase64String(): String {
    if (this.length > 100) {
        return this.take(40) + "*".repeat(20) + this.takeLast(40)
    }
    return this
}

fun String.addPrefixSuffix(prefix: String?, suffix: String?): String = when {
    !prefix.isNullOrEmpty() && !suffix.isNullOrEmpty() -> "$prefix $this $suffix"
    !prefix.isNullOrEmpty() -> "$prefix $this"
    !suffix.isNullOrEmpty() -> "$this $suffix"
    else -> this
}

fun String.formatToMonthYear(): String = if (this.length == 4 && this.all { it.isDigit() }) {
    val year = this.substring(0, 2)
    val month = this.substring(2, 4)
    "$month/$year"
} else {
    Timber.i("Input must be in 'yymm' format $this")
    this
}

fun Context.getSafeString(@StringRes resId: Int): String = if (resId != Resources.ID_NULL) getString(resId) else emptyString()

fun String.checkPathSlash() = if (this.startsWith("/")) removePrefix("/") else this

fun String.trimSpace(): String {
    if (this.isBlank() || this.startsWith(" ")) {
        return emptyString()
    }
    return this
}

fun String.maskPhoneNumberLasFour(): String = if (length > 4) {
    "*".repeat(length - 4) + takeLast(4)
} else {
    this
}

fun String.maskMiddleNameEmail(): String {
    val parts = split("@")
    if (parts.size != 2) return this

    val name = parts[0]
    val domain = parts[1]

    return if (name.length >= 5) {
        name.take(3) + "***" + name.takeLast(2) + "@$domain"
    } else {
        "***" + name.takeLast(2) + "@$domain"
    }
}

fun String?.toComparableString(): String = this?.trim()?.replace("\\s+".toRegex(), " ").orEmpty()

fun String?.formatWithInterval(interval: Double?): String {
    if (this.isNullOrEmpty() || interval == null) return this.orEmpty()

    val chunkSize = interval.toInt()
    if (chunkSize <= 0) return this

    val raw = this.filterNot { it == ' ' }
    return raw.chunked(chunkSize).joinToString(" ")
}

fun String.toGroupedCode(groupSize: Int = 3, separator: String = " "): String =
    this.chunked(groupSize).joinToString(separator)

fun String.formatAsEKtp(): String {
    val digitsOnly = this.filter { it.isDigit() }
    return digitsOnly.chunked(4).joinToString(" ")
}

/**
 * Takes the last part of a dot-separated string, typically used to extract
 * the route name from a fully qualified route string.
 *
 * For example:
 * - "com.example.app.MyScreen", becomes "MyScreen".
 * - "LaunchableScreee/{data}" becomes "LaunchableScreee/{data}".>
 *
 * If the string is null, empty, or does not contain a dot, it will return null
 * or the original string if no dot is present.
 *
 * @return The extracted route name, or null if the input is null or empty.
 */
fun String?.takeRouteName(): String? = this?.split(".")?.lastOrNull()

/**
 * Extracts the screen name from a URL-like string.
 * It takes the part of the string before the first '/' and then before the first '?'.
 *
 * For example:
 * - "screenName/somePath?param=value" becomes "screenName"
 * - "screenName?param=value" becomes "screenName"
 * - "screenName" remains "screenName"
 * - null returns null
 * - "/path" returns "" (empty string before the first slash)
 * - "?query" returns "" (empty string before the first question mark)
 *
 * @return The extracted screen name, or null if the input string is null.
 *         Returns an empty string if the relevant part before '/' or '?' is empty.
 */
fun String?.takeScreenName(): String? = this?.split("/")?.firstOrNull()?.split("?")?.firstOrNull()

fun String.cleanedText(): String {
    val cleanedText = this.replace(Regex("[^0-9]"), emptyString())
    return cleanedText
}

fun String.uriEncode(allowedText: String? = null): String = Uri.encode(this, allowedText)

fun String.uriDecode(): String = Uri.decode(this)

fun String.applyIf(condition: Boolean, thenElse: () -> String) = if (condition) this else thenElse()

/**
 * Format a string into Indonesian NIK grouping: 6-6-4 (e.g. "1111112222223333" -> "111111 222222 3333").
 * Non-digit characters are ignored. If input length is shorter than 16, the remaining
 * digits will appear in the last group.
 */
fun String.toNIKFormat(): String {
    val digits = this.filter { it.isDigit() }
    if (digits.isEmpty()) return this

    val groupSizes = listOf(6, 6, 4)
    val parts = mutableListOf<String>()
    var index = 0

    for (size in groupSizes) {
        if (index >= digits.length) break
        val end = minOf(index + size, digits.length)
        parts.add(digits.substring(index, end))
        index = end
    }

    // If there are remaining digits beyond 6+6+4, append them as the last part.
    if (index < digits.length) parts.add(digits.substring(index))

    return parts.joinToString(" ")
}

fun String.forceToHttps(): String = if (this.startsWith(HTTP_PROTOCOL)) {
    this.replaceFirst(HTTP_PROTOCOL, HTTPS_PROTOCOL)
} else {
    this
}

fun String?.maskExceptLastFourCharacter(): String {
    if (this.isNullOrBlank() || this.length <= 4) return this.orEmpty()
    return "*".repeat(this.length - 4) + this.takeLast(4)
}

fun String.toHex(): String = encodeToByteArray().joinToString("") { "%02X".format(it) }

fun String.hexToAscii(): String = chunked(2).map { it.toInt(16).toChar() }.joinToString("")

fun String.hexToBytes(): ByteArray = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

fun String.filterByRegex(regex: Regex): String = filter { regex.matches(it.toString()) }

fun String.normalizeSpaces(): String = replace(Regex("^\\s+"), "").replace(Regex("\\s{2,}"), " ")

fun String.normalizeMarkdownToHtml(): String = this
    // bold + italic
    .replace(Regex("\\*\\*_(.*?)_\\*\\*"), "<b><i>$1</i></b>")
    .replace(Regex("_(\\*\\*(.*?)\\*\\*)_"), "<i><b>$2</b></i>")
    // bold
    .replace(Regex("(?<!\\w)\\*\\*(.*?)\\*\\*(?!\\w)"), "<b>$1</b>")
    .replace(Regex("(?<!\\w)__(.*?)__(?!\\w)"), "<b>$1</b>")
    // italic
    .replace(Regex("(?<!\\w)_(.*?)_(?!\\w)"), "<i>$1</i>")
    .replace(Regex("(?<!\\w)\\*(.*?)\\*(?!\\w)"), "<i>$1</i>")

fun String.normalizeEscapedAnsi(): String = replace("\\033", "\u001B")

fun String.normalizeAnsiToHtml(): String {
    val ansiRegex = Regex("\u001B\\[(.*?)[mK]")

    val result = this
    var bold = false
    var italic = false
    var underline = false
    var color: String? = null

    return buildString {
        var currentIndex = 0

        ansiRegex.findAll(result).forEach { match ->
            val start = match.range.first

            if (start > currentIndex) {
                appendStyledHtml(
                    result.substring(currentIndex, start),
                    bold,
                    italic,
                    underline,
                    color,
                )
            }

            val codes = match.groupValues[1]
                .split(";")
                .mapNotNull { it.toIntOrNull() }

            codes.forEach { code ->
                when (code) {
                    0 -> {
                        bold = false
                        italic = false
                        underline = false
                        color = null
                    }

                    1 -> {
                        bold = true
                    }

                    3 -> {
                        italic = true
                    }

                    4 -> {
                        underline = true
                    }

                    in 30..37 -> {
                        color = ansiColorToHtml(code)
                    }
                }
            }

            currentIndex = match.range.last + 1
        }

        if (currentIndex < result.length) {
            appendStyledHtml(
                result.substring(currentIndex),
                bold,
                italic,
                underline,
                color,
            )
        }
    }
}

fun StringBuilder.appendWithEndSpace(text: String) {
    if (isNotEmpty()) append(space())
    append(text)
}

private fun StringBuilder.appendStyledHtml(
    text: String,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    color: String?,
) {
    var styled = text
    if (bold) styled = "<b>$styled</b>"
    if (italic) styled = "<i>$styled</i>"
    if (underline) styled = "<u>$styled</u>"
    if (color != null) styled = "<span style=\"color:$color\">$styled</span>"
    append(styled)
}

private fun ansiColorToHtml(code: Int): String = when (code) {
    30 -> "black"
    31 -> "red"
    32 -> "green"
    33 -> "yellow"
    34 -> "blue"
    35 -> "magenta"
    36 -> "cyan"
    37 -> "white"
    else -> "black"
}

fun String?.orEmptyIfNullLiteral(): String =
    takeUnless { it.equals("null", ignoreCase = true) }.orEmpty()

fun String.formatGrouped(
    firstGroup: Int,
    secondGroup: Int,
    separator: String = EMPTY_SPACE,
): String {
    val digits = filter(Char::isDigit)

    if (digits.isEmpty()) return this

    return buildString {
        when {
            digits.length <= firstGroup -> {
                append(digits)
            }

            digits.length <= firstGroup + secondGroup -> {
                append(digits.take(firstGroup))
                append(separator)
                append(digits.drop(firstGroup))
            }

            else -> {
                append(digits.take(firstGroup))
                append(separator)
                append(digits.substring(firstGroup, firstGroup + secondGroup))
                append(separator)
                append(digits.drop(firstGroup + secondGroup))
            }
        }
    }
}
