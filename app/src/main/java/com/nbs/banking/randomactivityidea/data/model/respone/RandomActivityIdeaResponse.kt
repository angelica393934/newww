package com.nbs.banking.randomactivityidea.data.model.respone

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RandomActivityIdeaResponse(
    @SerialName("activity")
    val activity: String?,
    @SerialName("availability")
    val availability: Double?,
    @SerialName("type")
    val type: String?,
    @SerialName("participants")
    val participants: Int?,
    @SerialName("price")
    val price: Double?,
    @SerialName("accessibility")
    val accessibility: String?,
    @SerialName("duration")
    val duration: String?,
    @SerialName("kidFriendly")
    val kidFriendly: Boolean?,
    @SerialName("link")
    val link: String?,
    @SerialName("key")
    val key: String?,
)
