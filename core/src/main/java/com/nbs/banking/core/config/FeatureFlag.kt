package com.nbs.banking.core.config


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.core.config.key.FlagKey
import com.nbs.banking.core.config.provider.ConfigProvider
import timber.log.Timber

object FeatureFlag {
    private lateinit var configProvider: ConfigProvider

    fun init(configProvider: ConfigProvider) {
        this.configProvider = configProvider
        Timber.tag("DUDIDAM").d("init: FeatureFlag")
        Timber.tag("DUDIDAM").d(isEnabled(FlagKey.NONE).toString())
    }

    fun isEnabled(flagKey: FlagKey, default: Boolean = false): Boolean {
        val result = this.isFeatureEnabled(flagKey, default)
        return result is FlagResult.Enabled
    }

    inline fun isEnabled(
        flagKey: FlagKey,
        default: Boolean = false,
        onEnabled: () -> Unit,
    ) {
        val result = isFeatureEnabled(flagKey, default)

        if (result is FlagResult.Enabled) {
            onEnabled.invoke()
        }
    }

    inline fun isEnabled(
        flagKey: FlagKey,
        default: Boolean = false,
        onDisabled: ((configDetail: ConfigDetail) -> Unit),
        onEnabled: () -> Unit,
    ) {
        val result = isFeatureEnabled(flagKey, default)
        if (result is FlagResult.Enabled) {
            onEnabled.invoke()
        } else if (result is FlagResult.Disabled) {
            onDisabled.invoke(result.configDetail)
        }
    }

    fun isFeatureEnabled(flagKey: FlagKey, default: Boolean = false): FlagResult {
        val flagConfig = if (::configProvider.isInitialized) {
            configProvider.getFlagConfig(flagKey, default)
        } else {
            Timber.i("Config has not been initialized")
            null
        }
        return if (flagConfig == null) {
            if (default) FlagResult.Enabled else FlagResult.Disabled(ConfigDetail())
        } else if (flagConfig.enabled && flagConfig.configDetail.state == ActivationState.ACTIVE) {
            FlagResult.Enabled
        } else {
            FlagResult.Disabled(flagConfig.configDetail)
        }
    }
}

sealed class FlagResult {
    data object Enabled : FlagResult()

    class Disabled(val configDetail: ConfigDetail) : FlagResult()
}

@Preview
@Composable
fun TestHideFeature() {
    LazyColumn {
        // outside composable scope
        FeatureFlag.isEnabled(FlagKey.NONE) {
            item { Text("Enabled 1") }
        }

        // inside composable scope
        item {
            FeatureFlag.isEnabled(
                flagKey = FlagKey.NONE,
                onDisabled = {
                    Text("Disabled 2")
                },
            ) {
                Text("Enabled 2")
            }
        }
    }
}
