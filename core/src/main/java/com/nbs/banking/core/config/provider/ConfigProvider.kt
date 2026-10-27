package com.nbs.banking.core.config.provider

import com.nbs.banking.core.config.FlagConfig
import com.nbs.banking.core.config.key.ConfigKey
import com.nbs.banking.core.config.key.FlagKey

interface ConfigProvider {
    fun getConfigVersion(): Int

    fun getString(key: ConfigKey): String

    fun getDouble(key: ConfigKey): Double

    fun getInt(key: ConfigKey): Int

    fun getLong(key: ConfigKey): Long

    fun <T> getObject(key: ConfigKey, type: Class<T>): T?

    fun getFlagConfig(flag: FlagKey, default: Boolean): FlagConfig?
}
