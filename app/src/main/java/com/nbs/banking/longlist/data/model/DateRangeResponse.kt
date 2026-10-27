package com.nbs.banking.longlist.data.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TransactionResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("date")
    @Contextual
    val date: LocalDate,
    @SerialName("title")
    val title: String,
    @SerialName("amountCents")
    val amountCents: Long,
)

@Serializable
data class DateRangeResponse<T>(val data: List<T>)