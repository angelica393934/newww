package com.nbs.banking.longlist.domain.usecase

import androidx.paging.PagingData
import com.nbs.banking.longlist.domain.LongListRepository
import com.nbs.banking.longlist.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

interface TransactionUseCase {
    operator fun invoke(from: LocalDate, to: LocalDate): Flow<PagingData<Transaction>>
}

class TransactionUseCaseImpl @Inject constructor(
    private val repository: LongListRepository
) : TransactionUseCase {
    override operator fun invoke(from: LocalDate, to: LocalDate): Flow<PagingData<Transaction>> {
        return repository.getTransactionsByDateRange(from = from, to = to)
    }
}