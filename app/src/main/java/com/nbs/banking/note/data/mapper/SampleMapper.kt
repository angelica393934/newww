package com.nbs.banking.note.data.mapper

import com.nbs.banking.coredatabase.entitiy.SampleEntity
import com.nbs.banking.note.domain.model.SampleNote

fun SampleEntity.toDomain(): SampleNote = SampleNote(
    id = id,
    note = note,
    createdAt = createdAt
)

fun SampleNote.toEntity(): SampleEntity = SampleEntity(
    id = id,
    note = note,
    createdAt = createdAt
)