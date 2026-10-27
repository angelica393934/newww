package com.nbs.banking.home.domain.dummyhome.usecase

import javax.inject.Inject

interface CheckHomeStatusUseCase {
    suspend operator fun invoke(): Boolean
}

class CheckHomeStatusUseCaseImpl @Inject constructor() : CheckHomeStatusUseCase {
    override suspend operator fun invoke(): Boolean {
        return true
    }
}
