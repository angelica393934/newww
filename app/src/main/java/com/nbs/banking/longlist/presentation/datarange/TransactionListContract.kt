package com.nbs.banking.longlist.presentation.datarange

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

sealed interface TransactionIntent {
    data class SetRange(val from: LocalDate, val to: LocalDate) : TransactionIntent
}

sealed interface TransactionEvent {
    data class RangeChanged(
        val from: LocalDate,
        val to: LocalDate
    ) : TransactionEvent
}

data class TransactionListState(
    val from: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(3, DateTimeUnit.MONTH),
    val to: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
)

sealed interface TransactionEffect {
    data class ShowMessage(val message: String) : TransactionEffect
}
