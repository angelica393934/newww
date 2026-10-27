package com.nbs.banking.randomactivityidea.data.remote

import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import retrofit2.Response
import retrofit2.http.GET

interface RandomActivityIdeaApiClient {
    @GET("random")
    suspend fun getRandomActivity(): Response<RandomActivityIdeaResponse>

}