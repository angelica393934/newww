package com.nbs.banking.core.di

import com.nbs.banking.core.encryption.AesEncryptionManager
import com.nbs.banking.core.encryption.EncryptionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object EncryptionModule {
    @Singleton
    @Provides
    fun provideEncryptionService(): EncryptionService = AesEncryptionManager()
}
