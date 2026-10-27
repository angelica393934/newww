package com.nbs.banking.note.data.local

import com.nbs.banking.coredatabase.dao.SampleDao
import com.nbs.banking.coredatabase.entitiy.SampleEntity
import com.nbs.banking.coredatabase.utils.catchDbExceptions
import com.nbs.banking.coredatabase.utils.safeDbCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SampleLocalDataSourceImpl @Inject constructor(
    private val sampleDao: SampleDao
) : SampleLocalDataSource {
    override fun getAll(): Flow<List<SampleEntity>> = sampleDao.getAll().catchDbExceptions()

    override suspend fun insert(entity: SampleEntity): Long =
        safeDbCall { sampleDao.insert(entity) }

    override suspend fun delete(id: Long) = safeDbCall {
        sampleDao.delete(id)
    }
}