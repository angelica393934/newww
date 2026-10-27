package com.nbs.banking.longlist.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageSizeResponse<T>(
    @SerialName("page")
    val page: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("data")
    val data: List<T>
)


@Serializable
data class LimitOffsetResponse<T>(
    @SerialName("limit")
    val limit: Int,
    @SerialName("offset")
    val offset: Int,
    @SerialName("data")
    val data: List<T>
)

@Serializable
data class CursorResponse<T>(
    @SerialName("data")
    val data: List<T>,
    @SerialName("nextCursor")
    val nextCursor: String? = null,
    @SerialName("prevCursor")
    val prevCursor: String? = null
)

@Serializable
data class NumberResponse(
    @SerialName("number")
    val number: Int,
)