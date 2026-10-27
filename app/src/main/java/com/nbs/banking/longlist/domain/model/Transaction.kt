package com.nbs.banking.longlist.domain.model

import kotlinx.datetime.LocalDate

data class Transaction(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val amountCents: Long,
)
