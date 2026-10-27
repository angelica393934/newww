package com.nbs.banking.longlist.di

import com.nbs.banking.longlist.data.LongListRepositoryImpl
import com.nbs.banking.longlist.data.remote.LongListFakeApiClient
import com.nbs.banking.longlist.data.remote.LongListRemoteDataSource
import com.nbs.banking.longlist.data.remote.LongListRemoteDataSourceImpl
import com.nbs.banking.longlist.domain.LongListRepository
import com.nbs.banking.longlist.domain.usecase.LongListUseCase
import com.nbs.banking.longlist.domain.usecase.LongListUseCaseImpl
import com.nbs.banking.longlist.domain.usecase.TransactionUseCase
import com.nbs.banking.longlist.domain.usecase.TransactionUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LongListModule {

    @Binds
    abstract fun bindLongListRemoteDataSource(
        impl: LongListRemoteDataSourceImpl
    ): LongListRemoteDataSource

    @Binds
    abstract fun bindLongListRepository(
        impl: LongListRepositoryImpl
    ): LongListRepository

    @Binds
    abstract fun bindLongListUseCase(
        impl: LongListUseCaseImpl
    ): LongListUseCase

    @Binds
    abstract fun bindTransactionUseCase(
        impl: TransactionUseCaseImpl
    ): TransactionUseCase

    companion object {
        @Provides
        fun provideFakeApi(): LongListFakeApiClient = LongListFakeApiClient()
    }
}
