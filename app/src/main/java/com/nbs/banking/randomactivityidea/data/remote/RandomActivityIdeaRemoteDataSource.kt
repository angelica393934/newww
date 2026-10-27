package com.nbs.banking.randomactivityidea.data.remote

import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import com.nbs.banking.core.base.RemoteDataSource

interface RandomActivityIdeaRemoteDataSource : RemoteDataSource{
    suspend fun getRandomActivity(): RandomActivityIdeaResponse
}