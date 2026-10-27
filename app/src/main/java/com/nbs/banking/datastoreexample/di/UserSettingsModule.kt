package com.nbs.banking.datastoreexample.di

import com.nbs.banking.datastoreexample.data.UserSettingsRepositoryImpl
import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import com.nbs.banking.datastoreexample.domain.usecase.ClearUserSettingsUseCase
import com.nbs.banking.datastoreexample.domain.usecase.ClearUserSettingsUseCaseImpl
import com.nbs.banking.datastoreexample.domain.usecase.GetUserSettingsEagerUseCase
import com.nbs.banking.datastoreexample.domain.usecase.GetUserSettingsEagerUseCaseImpl
import com.nbs.banking.datastoreexample.domain.usecase.GetUserSettingsUseCase
import com.nbs.banking.datastoreexample.domain.usecase.GetUserSettingsUseCaseImpl
import com.nbs.banking.datastoreexample.domain.usecase.ObserveUserSettingsUseCase
import com.nbs.banking.datastoreexample.domain.usecase.ObserveUserSettingsUseCaseImpl
import com.nbs.banking.datastoreexample.domain.usecase.SaveUserSettingUseCase
import com.nbs.banking.datastoreexample.domain.usecase.SaveUserSettingUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserSettingsModule {

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        impl: UserSettingsRepositoryImpl
    ): UserSettingsRepository

    @Binds
    @Singleton
    abstract fun bindObserveUserSettingsUseCase(
        impl: ObserveUserSettingsUseCaseImpl
    ): ObserveUserSettingsUseCase

    @Binds
    @Singleton
    abstract fun bindGetUserSettingsEagerUseCase(
        impl: GetUserSettingsEagerUseCaseImpl
    ): GetUserSettingsEagerUseCase

    @Binds
    @Singleton
    abstract fun bindGetUserSettingsUseCase(
        impl: GetUserSettingsUseCaseImpl
    ): GetUserSettingsUseCase

    @Binds
    @Singleton
    abstract fun bindSaveUserSettingUseCase(
        impl: SaveUserSettingUseCaseImpl
    ): SaveUserSettingUseCase

    @Binds
    @Singleton
    abstract fun bindClearUserSettingsUseCase(
        impl: ClearUserSettingsUseCaseImpl
    ): ClearUserSettingsUseCase
}
