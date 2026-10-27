package com.nbs.banking.home.domain.dummyhome.usecase

import javax.inject.Inject

interface DismissBannerUseCase {
    suspend operator fun invoke(bannerId: String): Boolean
}

class DismissBannerUseCaseImpl @Inject constructor() : DismissBannerUseCase {
    override suspend operator fun invoke(bannerId: String): Boolean {
        return true
    }
}
