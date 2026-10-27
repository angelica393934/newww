package com.nbs.banking.core.model

import androidx.annotation.Keep
import com.nbs.banking.core.extensions.emptyString

@Keep
data class ShareDescriptionData(
    val description: String = emptyString(),
)

@Keep
data class ShareDescriptionConfig(
    val description: ConfigLanguage = ConfigLanguage(),
)

@Keep
data class ConfigLanguage(
    val id: String = emptyString(),
    val en: String = emptyString(),
)

@Keep
fun ShareDescriptionConfig.toShareDescriptionData(locale: String = LanguageEnum.INDONESIAN.code): ShareDescriptionData {
    fun ConfigLanguage.getByLocale(): String = when (locale.lowercase()) {
        LanguageEnum.ENGLISH.code -> en
        LanguageEnum.INDONESIAN.code -> id
        else -> id
    }

    return ShareDescriptionData(
        description = description.getByLocale(),
    )
}
