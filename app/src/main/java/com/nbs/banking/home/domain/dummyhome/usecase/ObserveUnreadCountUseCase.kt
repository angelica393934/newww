package com.nbs.banking.home.domain.dummyhome.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ObserveUnreadCountUseCase {
    operator fun invoke(): Flow<Int>
}

class ObserveUnreadCountUseCaseImpl @Inject constructor() : ObserveUnreadCountUseCase {
    override operator fun invoke(): Flow<Int> = flow { emit(3) }
}
