package com.nbs.banking.core.di

import com.nbs.banking.core.eventbus.event.ApiErrorEventHandler
import com.nbs.banking.core.eventbus.event.ApiSuccessEventHandler
import com.nbs.banking.core.eventbus.event.LogoutEventHandler
import com.nbs.banking.core.eventbus.event.SilentPushEventHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventBusModule {
    @Provides
    @Singleton
    fun provideEventBusSilentPush(): SilentPushEventHandler = SilentPushEventHandler()

    @Provides
    @Singleton
    fun provideEventBusErrorHandler(): ApiErrorEventHandler = ApiErrorEventHandler()

    @Provides
    @Singleton
    fun provideEventBusTriggerHandler(): ApiSuccessEventHandler = ApiSuccessEventHandler()

    @Provides
    @Singleton
    fun provideEventBusLogoutHandler(): LogoutEventHandler = LogoutEventHandler()
}
