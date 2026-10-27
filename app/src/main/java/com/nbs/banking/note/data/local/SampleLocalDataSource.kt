package com.nbs.banking.note.data.local

import com.nbs.banking.core.base.LocalDataSource
import com.nbs.banking.coredatabase.entitiy.SampleEntity
import kotlinx.coroutines.flow.Flow

interface SampleLocalDataSource : LocalDataSource {

    fun getAll(): Flow<List<SampleEntity>>

    suspend fun insert(entity: SampleEntity): Long

    suspend fun delete(id: Long)
}