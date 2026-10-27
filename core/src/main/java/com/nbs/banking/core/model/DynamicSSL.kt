package com.nbs.banking.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DynamicSSL(
    @SerialName("lastUpdatedAt")
    val lastUpdatedAt: String,
    @SerialName("pairs")
    val pairs: List<PairItem>,
)

@Serializable
data class PairItem(
    @SerialName("domain")
    val domain: String,
    @SerialName("fingerprints")
    val fingerprints: List<String>,
)
