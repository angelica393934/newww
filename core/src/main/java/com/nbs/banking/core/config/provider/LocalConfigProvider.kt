package com.nbs.banking.core.config.provider

import android.content.Context
import com.nbs.banking.core.config.FlagConfig
import com.nbs.banking.core.config.key.ConfigKey
import com.nbs.banking.core.config.key.FlagKey
import com.nbs.banking.core.extensions.toStringNullOrEmpty
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber

class LocalConfigProvider(
    context: Context,
    private val json: Json,
) : ConfigProvider {
    private val flagObject = ConcurrentHashMap<String, Any>()
    private val configObject = ConcurrentHashMap<String, Any>()

    companion object {
        private const val PREFIX_FLAG = "flag_"
    }

    init {
//        loadXmlInto(context, R.xml.firebase_remote_config_defaults)
        testConfig()
    }

    private fun loadXmlInto(context: Context, xmlResId: Int) {
        runCatching {
            val parser = context.resources.getXml(xmlResId)
            var key: String? = null
            var value: String? = null
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "key" -> key = parser.nextText().trim()
                            "value" -> value = parser.nextText().trim()
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "entry") {
                            if (key != null && value != null) {
                                if (key.startsWith(PREFIX_FLAG)) {
                                    flagObject[key] = value
                                } else {
                                    configObject[key] = value
                                }
                            }
                            key = null
                            value = null
                        }
                    }
                }
                eventType = parser.next()
            }
        }.onFailure { e ->
            when (e) {
                is IOException -> Timber.e(e, "Error reading XML resource: ${e.message}")
                is XmlPullParserException -> Timber.e(e, "Error parsing XML resource: ${e.message}")
                else -> Timber.e(e, "Unexpected error while loading XML resource: ${e.message}")
            }
        }.onSuccess {
            Timber.i("Local config loaded successfully: %d flags, %d configs", flagObject.size, configObject.size)
        }
    }

    private fun testConfig() {
        // for test config
    }

    override fun getConfigVersion(): Int = 0

    override fun getString(key: ConfigKey): String = configObject[key.key].toStringNullOrEmpty()

    override fun getDouble(key: ConfigKey): Double = getString(key).toDoubleOrNull() ?: 0.0

    override fun getInt(key: ConfigKey): Int = getString(key).toIntOrNull() ?: 0

    override fun getLong(key: ConfigKey) = getString(key).toLongOrNull() ?: 0L

    @Suppress("UNCHECKED_CAST")
    override fun <T> getObject(key: ConfigKey, type: Class<T>): T? {
        val jsonString = getString(key)
        if (jsonString.isEmpty()) return null
        return runCatching {
            json.decodeFromString(json.serializersModule.serializer(type), jsonString) as? T
        }.getOrNull()
    }

    override fun getFlagConfig(flag: FlagKey, default: Boolean): FlagConfig? {
        val jsonString = flagObject[flag.key].toStringNullOrEmpty()
        if (jsonString.isEmpty()) return FlagConfig(enabled = default)
        return runCatching { json.decodeFromString<FlagConfig>(jsonString) }.getOrNull() ?: FlagConfig(enabled = default)
    }
}
