package com.nbs.banking.randomactivityidea.domain

import com.nbs.banking.core.base.BaseRepository
import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea

interface RandomActivityIdeaRepository : BaseRepository {
    suspend fun getRandomActivity(): RandomActivityIdea
}