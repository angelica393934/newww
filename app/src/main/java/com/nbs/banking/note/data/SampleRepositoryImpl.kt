package com.nbs.banking.note.data

import com.nbs.banking.coredatabase.entitiy.SampleEntity
import com.nbs.banking.note.data.local.SampleLocalDataSource
import com.nbs.banking.note.data.mapper.toDomain
import com.nbs.banking.note.data.mapper.toEntity
import com.nbs.banking.note.domain.SampleRepository
import com.nbs.banking.note.domain.model.SampleNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    override val localDataSource: SampleLocalDataSource,
) : SampleRepository {
    override fun getAll(): Flow<List<SampleNote>> {
        return localDataSource.getAll().map {
            it.map(SampleEntity::toDomain)
        }
    }

    override suspend fun insert(note: SampleNote): Long {
        return localDataSource.insert(note.toEntity())
    }

    override suspend fun delete(id: Long) {
        localDataSource.delete(id)
    }

}