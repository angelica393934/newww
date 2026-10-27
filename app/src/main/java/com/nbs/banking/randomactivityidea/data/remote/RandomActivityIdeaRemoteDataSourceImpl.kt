package com.nbs.banking.randomactivityidea.data.remote

import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import com.nbs.banking.corenetwork.utils.safeApiCall
import javax.inject.Inject

class RandomActivityIdeaRemoteDataSourceImpl @Inject constructor(private val apiClient: RandomActivityIdeaApiClient) :
    RandomActivityIdeaRemoteDataSource {
    override suspend fun getRandomActivity(): RandomActivityIdeaResponse {
        return safeApiCall { apiClient.getRandomActivity() }
    }
}