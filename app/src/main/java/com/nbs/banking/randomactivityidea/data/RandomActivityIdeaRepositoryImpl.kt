package com.nbs.banking.randomactivityidea.data

import com.nbs.banking.randomactivityidea.data.mapper.toDomain
import com.nbs.banking.randomactivityidea.data.remote.RandomActivityIdeaRemoteDataSource
import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import com.nbs.banking.randomactivityidea.domain.RandomActivityIdeaRepository
import javax.inject.Inject

class RandomActivityIdeaRepositoryImpl @Inject constructor(
    override val remoteDataSource: RandomActivityIdeaRemoteDataSource
) : RandomActivityIdeaRepository {

    override suspend fun getRandomActivity(): RandomActivityIdea {
        val response = remoteDataSource.getRandomActivity()
        return response.toDomain()
    }
}