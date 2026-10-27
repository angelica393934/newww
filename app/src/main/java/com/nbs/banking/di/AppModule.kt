package com.nbs.banking.di

import com.nbs.banking.navigation.deeplink.DeepLinkPolicy
import com.nbs.banking.navigation.deeplink.DeepLinkResolver
import com.nbs.banking.utils.deeplink.appDeepLinkMatchers
import com.nbs.banking.utils.deeplink.appDeepLinkPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDeepLinkResolver(): DeepLinkResolver =
        DeepLinkResolver(appDeepLinkMatchers)

    @Provides
    @Singleton
    fun provideDeepLinkPolicy(): DeepLinkPolicy = appDeepLinkPolicy
}