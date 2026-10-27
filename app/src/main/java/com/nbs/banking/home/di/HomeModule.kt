package com.nbs.banking.home.di

import com.nbs.banking.home.domain.dummyhome.usecase.CheckHomeStatusUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.CheckHomeStatusUseCaseImpl
import com.nbs.banking.home.domain.dummyhome.usecase.DismissBannerUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.DismissBannerUseCaseImpl
import com.nbs.banking.home.domain.dummyhome.usecase.FetchHomeContentUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.FetchHomeContentUseCaseImpl
import com.nbs.banking.home.domain.dummyhome.usecase.ObserveUnreadCountUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.ObserveUnreadCountUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    abstract fun bindFetchHomeContentUseCase(
        impl: FetchHomeContentUseCaseImpl
    ): FetchHomeContentUseCase

    @Binds
    abstract fun bindCheckHomeStatusUseCase(
        impl: CheckHomeStatusUseCaseImpl
    ): CheckHomeStatusUseCase

    @Binds
    abstract fun bindObserveUnreadCountUseCase(
        impl: ObserveUnreadCountUseCaseImpl
    ): ObserveUnreadCountUseCase

    @Binds
    abstract fun bindDismissBannerUseCase(
        impl: DismissBannerUseCaseImpl
    ): DismissBannerUseCase
}