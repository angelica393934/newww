package com.nbs.banking.note.domain.model

import kotlin.time.Instant

data class SampleNote(
    val id: Long = 0,
    val note: String,
    val createdAt: Instant
)
