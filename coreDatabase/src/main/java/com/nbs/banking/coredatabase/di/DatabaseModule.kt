package com.nbs.banking.coredatabase.di

import android.content.Context
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.nbs.banking.core.di.IoDispatcher
import com.nbs.banking.coredatabase.AppDatabase
import com.nbs.banking.coredatabase.dao.SampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import net.zetetic.database.sqlcipher.driver.SQLCipherDriver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    //TODO: PUT IT IN SECURE PLACE, THIS IS ONLY FOR SAMPLE!!!
    private const val DB_CIPHER_KEY = "nbs_banking_cipher"
    private val passphrase: ByteArray = DB_CIPHER_KEY.toByteArray()

    @Provides
    @Singleton
    @StandardDriver
    fun provideDriver(): SQLiteDriver = AndroidSQLiteDriver()

    @Provides
    @Singleton
    @CipherDriver
    fun provideCipherDriver(): SQLiteDriver = SQLCipherDriver(passphrase, null, null)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @CipherDriver sqlLiteDriver: SQLiteDriver,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AppDatabase = AppDatabase.getInstance(context, sqlLiteDriver, ioDispatcher)

    @Provides
    fun provideSampleDao(database: AppDatabase): SampleDao = database.sampleDao()
}