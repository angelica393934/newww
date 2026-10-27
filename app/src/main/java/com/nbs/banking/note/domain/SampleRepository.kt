package com.nbs.banking.note.domain

import com.nbs.banking.core.base.BaseRepository
import com.nbs.banking.note.domain.model.SampleNote
import kotlinx.coroutines.flow.Flow

interface SampleRepository : BaseRepository {
    fun getAll(): Flow<List<SampleNote>>

    suspend fun insert(note: SampleNote): Long

    suspend fun delete(id: Long)
}