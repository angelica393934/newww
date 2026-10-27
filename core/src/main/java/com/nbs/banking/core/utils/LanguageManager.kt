package com.nbs.banking.core.utils

//import android.app.Activity
//import android.content.Context
//import android.content.res.Configuration
//import android.os.Build
//import androidx.appcompat.app.AppCompatDelegate
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.compositionLocalOf
//import androidx.compose.runtime.getValue
//import androidx.core.os.LocaleListCompat
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.danamon.mobile.core.R
//import com.danamon.mobile.core.caching.CacheManager
//import com.danamon.mobile.core.model.LanguageConstant
//import com.danamon.mobile.core.model.LanguageData
//import com.danamon.mobile.core.model.LanguageEnum
//import com.danamon.mobile.core.storage.DeviceStorage
//import com.danamon.mobile.core.storage.handler.DataStoreHandler
//import java.util.Locale
//import javax.inject.Inject
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import timber.log.Timber
//
//class LanguageManager @Inject constructor(
//    private val contextProvider: ContextProvider,
//    val deviceStorage: DeviceStorage,
//    private val appScope: CoroutineScope,
//    private val dataStoreHandler: DataStoreHandler,
//) {
//    private val tag = "LanguageManager"
//
//    private val _languageFlow = MutableStateFlow(LanguageConstant.DEFAULT_LANGUAGE)
//    val languageFlow = _languageFlow.asStateFlow()
//
//    companion object {
//        private const val TAG = "LanguageManager"
//        private var instance: LanguageManager? = null
//
//        fun init(manager: LanguageManager) {
//            Timber.tag(TAG).d("init")
//            instance = manager
//        }
//
//        fun updateContext(context: Context): Context {
//            Timber.tag(TAG).d("updateContext")
//
//            val languageManager = instance
//            val updatedContext = languageManager?.launchLanguage(context) ?: context
//            return updatedContext
//        }
//    }
//
//    fun recreateActivity(context: Context) {
//        (context as? Activity)?.recreate()
//    }
//
//    fun launchLanguage(context: Context): Context {
//        val targetLanguage = getCurrentLanguage(context)
//        Timber.tag(tag).d("launchLanguage to $targetLanguage")
//
//        val updatedContext = applyLocaleLanguage(context, targetLanguage)
//        return updatedContext
//    }
//
//    suspend fun changeLanguage(context: Context, languageCode: String? = null): Boolean {
//        val targetLanguage = languageCode ?: getCurrentLanguage(context)
//        val currentLanguage = getCurrentLocaleLanguage()
//
//        Timber.tag(tag).d("changeLanguageSuspend to $targetLanguage")
//        if (targetLanguage.equals(currentLanguage, ignoreCase = true)) {
//            Timber.tag(tag).d("Language already applied: $targetLanguage")
//            return false
//        }
//
//        applyLocaleLanguage(context, targetLanguage)
//
//        if (languageCode != null) {
//            saveLanguagePreference(targetLanguage)
//        }
//
//        return true
//    }
//
//    fun changeLanguageConfig(context: Context, languageCode: String? = null): Boolean {
//        val targetLanguage = languageCode ?: getCurrentLanguage(context)
//        val currentLanguage = getCurrentLocaleLanguage()
//
//        Timber.tag(tag).d("changeLanguageConfig to $targetLanguage")
//        if (targetLanguage.equals(currentLanguage, ignoreCase = true)) {
//            Timber.tag(tag).d("Language already applied: $targetLanguage")
//            return false
//        }
//
//        applyLocaleLanguage(context, targetLanguage)
//
//        if (languageCode != null) {
//            appScope.launch {
//                withContext(Dispatchers.IO) {
//                    saveLanguagePreference(targetLanguage)
//                }
//            }
//        }
//
//        return true
//    }
//
//    private fun applyLocaleLanguage(context: Context, languageCode: String): Context {
//        Timber.tag(tag).d("applyLocaleLanguage")
//
//        val localeListCompat = LocaleListCompat.forLanguageTags(languageCode)
//        AppCompatDelegate.setApplicationLocales(localeListCompat)
//
//        val locale = Locale.forLanguageTag(languageCode)
//        Locale.setDefault(locale)
//
//        val configuration = Configuration(context.resources.configuration)
//        configuration.setLocale(locale)
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
//            @Suppress("DEPRECATION")
//            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
//        }
//        val updatedContext = context.createConfigurationContext(configuration)
//
//        _languageFlow.value = languageCode
//
//        contextProvider.updateContext(updatedContext)
//        return updatedContext
//    }
//
//    fun getCurrentLanguage(context: Context): String {
//        val languageLegacy = getLegacyAppLanguage()
//        val localeLanguage = getCurrentLocaleLanguage()
//
//        Timber.tag(tag).d("getCurrentLanguage: legacy $languageLegacy")
//        Timber.tag(tag).d("getCurrentLanguage: locale $localeLanguage")
//
//        if (languageLegacy != localeLanguage) {
//            Timber.tag(tag).d("not same")
//            Timber.tag(tag).d("context: $context")
//            Timber.tag(tag).d("contextProvider: ${contextProvider.getContext()}")
//        }
//
//        return languageLegacy
//    }
//
//    private fun getCurrentLocaleLanguage(): String {
//        val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
//        val languageCode = getLanguageToApply(locale.toLanguageTag())
//        return languageCode
//    }
//
//    private fun getLegacyAppLanguage(): String = deviceStorage.getLanguage()
//
//    private suspend fun saveLanguagePreference(languageCode: String) {
//        // clear cache
//        CacheManager.invalidateAllCache(preserveWhitelist = false)
//        dataStoreHandler.clearDataKeysString(
//            DataStoreKeys.DASHBOARD_SEARCH_MENU.name,
//            DataStoreKeys.DASHBOARD_SEARCH_FAVORITE_CONTACT.name,
//            DataStoreKeys.DASHBOARD_RECENT_SEARCH.name,
//        )
//
//        // save language
//        Timber.tag(tag).d("saveLanguagePreference: $languageCode")
//        deviceStorage.saveLanguage(languageCode)
//    }
//
//    private fun getLanguageToApply(languageCode: String, supportedLanguages: List<LanguageData> = getAllLanguages()): String {
//        val locale = Locale.forLanguageTag(languageCode)
//        val normalizeLanguageCode = when (val lang = locale.language.lowercase(Locale.ROOT)) {
//            "in" -> "id"
//            else -> lang
//        }
//
//        if (supportedLanguages.map { it.code }.contains(normalizeLanguageCode)) {
//            return normalizeLanguageCode
//        }
//
//        return LanguageConstant.DEFAULT_LANGUAGE
//    }
//
//    fun getAllLanguages(): List<LanguageData> = listOf(
//        LanguageData(
//            code = LanguageEnum.INDONESIAN.code,
//            name = LanguageEnum.INDONESIAN.naming,
//            R.drawable.ic_indonesia_circle_flag,
//        ),
//        LanguageData(
//            code = LanguageEnum.ENGLISH.code,
//            name = LanguageEnum.ENGLISH.naming,
//            R.drawable.ic_britain_circle_flag,
//        ),
////        LanguageData(
////            code = LanguageEnum.JAPANESE.code,
////            name = LanguageEnum.JAPANESE.naming,
////            R.drawable.ic_japan_circle_flag,
////        ),
//    )
//}
//
//// region LocalAppLanguage
//val LocalAppLanguage = compositionLocalOf { LanguageConstant.DEFAULT_LANGUAGE }
//
//@Composable
//fun rememberLanguage(languageManager: LanguageManager): String {
//    val currentLanguage by languageManager.languageFlow.collectAsStateWithLifecycle()
//    Timber.tag("rememberLanguage").d("languageLocaleChanged: $currentLanguage")
//    return currentLanguage
//}
//// end region
