package com.nbs.banking.home.domain.dummyhome.usecase

import com.nbs.banking.home.domain.dummyhome.model.HomeContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface FetchHomeContentUseCase {
    operator fun invoke(): Flow<HomeContent>
}

class FetchHomeContentUseCaseImpl @Inject constructor() : FetchHomeContentUseCase {
    override operator fun invoke(): Flow<HomeContent> = flow {
        emit(HomeContent(title = "Welcome Back", description = "Here is your account overview", "Johnson"))
    }
}