package com.nbs.banking.randomactivityidea.domain.usecase

import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import com.nbs.banking.randomactivityidea.domain.RandomActivityIdeaRepository
import javax.inject.Inject

interface GetRandomActivityUseCase {
    suspend operator fun invoke(): RandomActivityIdea
}

class GetRandomActivityUseCaseImpl @Inject constructor(
    private val randomActivityIdeaRepository: RandomActivityIdeaRepository
) : GetRandomActivityUseCase {
    override suspend fun invoke(): RandomActivityIdea = randomActivityIdeaRepository.getRandomActivity()
}
