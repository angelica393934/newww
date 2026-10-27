package com.nbs.banking.longlist.domain.usecase

import androidx.paging.PagingData
import com.nbs.banking.longlist.domain.LongListRepository
import com.nbs.banking.longlist.domain.model.NumberItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LongListUseCase {
    operator fun invoke(): Flow<PagingData<NumberItem>>
}

class LongListUseCaseImpl @Inject constructor(
    private val repository: LongListRepository
) : LongListUseCase {
    override operator fun invoke(): Flow<PagingData<NumberItem>> {
        return repository.getLongListOffsetLimit()
    }
}