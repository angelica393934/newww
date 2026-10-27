package com.nbs.banking.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nbs.banking.core.datastore.PreferenceDataStore
import com.nbs.banking.core.datastore.PreferenceDataStoreImpl
import com.nbs.banking.core.datastore.SecuredPreferenceDataStore
import com.nbs.banking.core.datastore.SecuredPreferenceDataStoreImpl
import com.nbs.banking.core.encryption.EncryptionKeyManager
import com.nbs.banking.core.encryption.Encryptor
import com.nbs.banking.core.encryption.TinkEncryptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NormalDataStoreType

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SecuredPreferenceDataStoreType

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Singleton
    @Binds
    abstract fun bindEncryptor(
        impl: TinkEncryptor
    ): Encryptor

    @Binds
    @Singleton
    abstract fun bindEncryptionKeyManager(
        impl: TinkEncryptor
    ): EncryptionKeyManager

    @Binds
    @Singleton
    abstract fun bindPreferenceDataStore(
        impl: PreferenceDataStoreImpl
    ): PreferenceDataStore

    @Singleton
    @Binds
    abstract fun bindSecuredPreferenceDataStore(
        impl: SecuredPreferenceDataStoreImpl
    ): SecuredPreferenceDataStore

    companion object {
        private const val PREFERENCES_NAME = "nbs_banking_datastore"
        private const val SECURE_PREFERENCES_NAME = "nbs_banking_secure_datastore"

        @Singleton
        @Provides
        @NormalDataStoreType
        fun provideDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler {
                emptyPreferences()
            },
        )

        @Singleton
        @Provides
        @SecuredPreferenceDataStoreType
        fun provideSecuredDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(SECURE_PREFERENCES_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler {
                emptyPreferences()
            },
        )
    }
}
