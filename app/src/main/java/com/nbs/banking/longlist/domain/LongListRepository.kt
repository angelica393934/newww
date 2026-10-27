package com.nbs.banking.longlist.domain

import androidx.paging.PagingData
import com.nbs.banking.core.base.BaseRepository
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.longlist.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface LongListRepository : BaseRepository {
    fun getLongListPageSized(): Flow<PagingData<NumberItem>>
    fun getLongListOffsetLimit(): Flow<PagingData<NumberItem>>
    fun getLongListCursor(): Flow<PagingData<NumberItem>>
    fun getTransactionsByDateRange(
        from: LocalDate, to: LocalDate
    ): Flow<PagingData<Transaction>>
}
