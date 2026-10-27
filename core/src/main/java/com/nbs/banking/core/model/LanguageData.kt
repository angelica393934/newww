package com.nbs.banking.core.model

data class LanguageData(
    val code: String,
    val name: String,
    val flagResourceId: Int,
    var isSelected: Boolean = false,
)

enum class LanguageEnum(
    val code: String,
    val country: String,
    val naming: String,
) {
    INDONESIAN("id", "ID", "Bahasa Indonesia"),
    ENGLISH("en", "US", "English"),
    JAPANESE("ja", "JP", "Japanese"),
    ;

    companion object {
        fun findTypeByCode(code: String): LanguageEnum = entries.find { it.code == code } ?: INDONESIAN

        fun findCountryByCode(code: String): String = entries.find { it.code.equals(code, ignoreCase = true) }?.country ?: INDONESIAN.country
    }
}

//val LanguageEnum.locale: Locale
//    get() = Locale(code, country).orDefault(Locale.US)

object LanguageConstant {
    const val KEY = "CHANGE_LANGUAGE"
    val DEFAULT_LANGUAGE = LanguageEnum.INDONESIAN.code
}
