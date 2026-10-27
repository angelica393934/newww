package com.nbs.banking.core.config.provider

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.nbs.banking.core.config.FlagConfig
import com.nbs.banking.core.config.key.ConfigKey
import com.nbs.banking.core.config.key.FlagKey
import com.nbs.banking.core.config.utils.addOnConfigUpdateListener
import com.nbs.banking.core.config.utils.fetchAndActivate
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import timber.log.Timber

private const val FIREABSE_CONFIG_PROVIDER_TAG = "FirebaseConfigProvider"

class FirebaseConfigProvider(
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig,
    private val json: Json,
) : ConfigProvider {
    private val setting = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 1
        fetchTimeoutInSeconds = 10
    }

    init {
        with(remoteConfig) {
//            setDefaultsAsync(R.xml.firebase_remote_config_defaults)
            setConfigSettingsAsync(setting).addOnSuccessListener {
                Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d("Firebase config default value has been set")
            }
        }
        initialFetch()
        testConfig()
    }

    private fun testConfig() {
        // For test config
    }

    private fun initialFetch() {
        Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d("initialFetch: Init remote fetching")

        remoteConfig.fetchAndActivate(
            onComplete = {
                Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d("fetchAndActivate: activate $it, config version: ${getConfigVersion()}")
            },
            onFailure = {
                Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d("fetchAndActivate: failed activate $it")
            },
        )

        remoteConfig.addOnConfigUpdateListener(
            onUpdate = { config ->
                Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d("configUpdate: updated config fetched ${config.updatedKeys.joinToString()}")
                remoteConfig.activate()
            },
            onError = { e ->
                Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d("configUpdate: failed to update config, ${e.code} ${e.message}")
            },
        )
    }

    override fun getConfigVersion(): Int = 0

    override fun getString(key: ConfigKey): String = remoteConfig.getString(key.key)

    override fun getDouble(key: ConfigKey): Double = remoteConfig.getDouble(key.key)

    override fun getInt(key: ConfigKey): Int = remoteConfig.getLong(key.key).toInt()

    override fun getLong(key: ConfigKey): Long = remoteConfig.getLong(key.key)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getObject(key: ConfigKey, type: Class<T>): T? {
        val jsonString = getString(key)
        if (jsonString.isEmpty()) return null
        return runCatching {
            json.decodeFromString(json.serializersModule.serializer(type), jsonString) as? T
        }.getOrNull()
    }

    override fun getFlagConfig(flag: FlagKey, default: Boolean): FlagConfig {
        val jsonString = remoteConfig.getString(flag.key)
        Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d(flag.key)
        Timber.tag(FIREABSE_CONFIG_PROVIDER_TAG).d(jsonString)
        if (jsonString.isEmpty()) return FlagConfig(enabled = default)
        return runCatching { json.decodeFromString<FlagConfig>(jsonString) }.getOrNull() ?: FlagConfig(enabled = default)
    }
}
