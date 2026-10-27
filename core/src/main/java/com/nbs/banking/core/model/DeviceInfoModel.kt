package com.nbs.banking.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfoModel(
    @SerialName("platform")
    val platform: String = "",
    @SerialName("deviceId")
    val deviceId: String = "",
    @SerialName("packageName")
    val packageName: String = "",
    @SerialName("appVersion")
    val appVersion: String = "",
    @SerialName("appVersionCode")
    val appVersionCode: String = "",
    @SerialName("deviceModel")
    val deviceModel: String = "",
    @SerialName("osVersion")
    val osVersion: String = "",
    @SerialName("manufacturer")
    val manufacturer: String = "",
)
