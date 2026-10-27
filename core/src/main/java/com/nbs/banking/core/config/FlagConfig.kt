package com.nbs.banking.core.config

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FlagConfig(
    val enabled: Boolean = false,
    val configDetail: ConfigDetail = ConfigDetail(),
)

@Keep
@Serializable
data class ConfigDetail(
    val state: ActivationState = ActivationState.DISABLED,
    val message: String = "",
)

@Keep
@Serializable
enum class ActivationState {
    DISABLED,
    MAINTENANCE,
    ACTIVE,
    INELIGIBLE,
}
