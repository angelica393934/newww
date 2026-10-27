package com.nbs.banking.core.model

import androidx.annotation.Keep
import com.nbs.banking.core.extensions.emptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class NotificationData(
    @SerialName("id")
    val id: String? = null,
    @SerialName("from")
    val topic: String? = null,
    @SerialName("body")
    val body: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("actions")
    val actions: String? = null,
    @SerialName("deeplink")
    val deeplink: String? = null,
    @SerialName("customAction")
    val customActions: String? = null,
    @SerialName("isShowNotification")
    val isShowNotification: Boolean? = true,
)

@Keep
@Serializable
data class CustomActions(
    @SerialName("actionType")
    val actionType: String? = emptyString(),
    @SerialName("actionParameters")
    val actionParameters: Map<String, String>? = emptyMap(),
)
