package com.nbs.banking.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("payload")
    val payload: T?,
    @SerialName("success")
    val success: Boolean?,
    @SerialName("timeStamp")
    val timeStamp: String?,
    @SerialName("correlationId")
    val correlationId: String?,
    @SerialName("token")
    val token: TokenResponse?,
    @SerialName("errors")
    val errors: ErrorResponse?,
)

@Serializable
data class TokenResponse(
    @SerialName("accessToken")
    val accessToken: String?,
    @SerialName("accessTokenIat")
    val accessTokenCrt: Long,
    @SerialName("accessTokenExp")
    val accessTokenExp: Long,
    @SerialName("refreshToken")
    val refreshToken: String?,
    @SerialName("refreshTokenIat")
    val refreshTokenCrt: Long,
    @SerialName("refreshTokenExp")
    val refreshTokenExp: Long,
)
