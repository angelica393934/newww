package com.nbs.banking.coredatabase.entitiy

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "sample_notes")
data class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val note: String,
    val createdAt: Instant
)