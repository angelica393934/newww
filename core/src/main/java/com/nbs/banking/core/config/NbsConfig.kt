package com.nbs.banking.core.config

import com.nbs.banking.core.config.key.ConfigKey
import com.nbs.banking.core.config.provider.ConfigProvider

object NbsConfig {
    private lateinit var configProvider: ConfigProvider

    fun init(configProvider: ConfigProvider) {
        this.configProvider = configProvider
    }

    fun getString(key: ConfigKey): String = configProvider.getString(key)

    fun getDouble(key: ConfigKey): Double = configProvider.getDouble(key)

    fun getInt(key: ConfigKey): Int = configProvider.getInt(key)

    fun getLong(key: ConfigKey): Long = configProvider.getLong(key)

    fun <T> getObject(key: ConfigKey, type: Class<T>): T? = if (::configProvider.isInitialized) {
        configProvider.getObject(key, type)
    } else {
        null
    }
}
