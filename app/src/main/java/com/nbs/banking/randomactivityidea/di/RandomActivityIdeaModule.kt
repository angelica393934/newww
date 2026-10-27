package com.nbs.banking.randomactivityidea.di

import com.nbs.banking.randomactivityidea.data.remote.RandomActivityIdeaApiClient
import com.nbs.banking.randomactivityidea.data.remote.RandomActivityIdeaRemoteDataSource
import com.nbs.banking.randomactivityidea.data.remote.RandomActivityIdeaRemoteDataSourceImpl
import com.nbs.banking.randomactivityidea.data.RandomActivityIdeaRepositoryImpl
import com.nbs.banking.randomactivityidea.domain.RandomActivityIdeaRepository
import com.nbs.banking.randomactivityidea.domain.usecase.GetRandomActivityUseCase
import com.nbs.banking.randomactivityidea.domain.usecase.GetRandomActivityUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RandomActivityIdeaModule {

    @Binds
    abstract fun bindRandomActivityIdeaRemoteDataSource(
        impl: RandomActivityIdeaRemoteDataSourceImpl
    ): RandomActivityIdeaRemoteDataSource

    @Binds
    abstract fun bindRandomActivityIdeaRepository(
        impl: RandomActivityIdeaRepositoryImpl
    ): RandomActivityIdeaRepository

    @Binds
    abstract fun bindGetRandomActivityUseCase(
        impl: GetRandomActivityUseCaseImpl
    ): GetRandomActivityUseCase

    companion object {
        @Provides
        @Singleton
        fun provideRandomActivityIdeaApiClient(retrofit: Retrofit): RandomActivityIdeaApiClient =
            retrofit.create(RandomActivityIdeaApiClient::class.java)
    }
}
