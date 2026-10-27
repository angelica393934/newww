package com.nbs.banking.coredatabase

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.ColumnTypeConverters
import androidx.sqlite.SQLiteDriver
import com.nbs.banking.core.di.IoDispatcher
import com.nbs.banking.coredatabase.converter.InstantConverter
import com.nbs.banking.coredatabase.dao.SampleDao
import com.nbs.banking.coredatabase.entitiy.SampleEntity
import kotlinx.coroutines.CoroutineDispatcher
import net.zetetic.database.sqlcipher.driver.SQLCipherDriver

private object DbConfig {
    const val DB_NAME = "nbs_banking_db"
    const val DATABASE_VERSION = 1
}

@Database(
    entities = [
        SampleEntity::class,
    ],
    version = DbConfig.DATABASE_VERSION,
    exportSchema = false,
)
@ColumnTypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao

    companion object {

        @Volatile
        private var dBankProDatabaseInstance: AppDatabase? = null

        //TODO: PUT IT IN SECURE PLACE, THIS IS ONLY FOR SAMPLE!!!
        private const val DB_CIPHER_KEY = "nbs_banking_cipher"

        fun getInstance(
            context: Context,
            sqlLiteDriver: SQLiteDriver,
            @IoDispatcher ioDispatcher: CoroutineDispatcher
        ): AppDatabase =
            dBankProDatabaseInstance ?: synchronized(this) {
                val passphrase: ByteArray = DB_CIPHER_KEY.toByteArray()
                val driver = SQLCipherDriver(
                    passphrase,
                    null,
                    null
                )

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DbConfig.DB_NAME,
                )
                    .setDriver(sqlLiteDriver)
                    .setQueryCoroutineContext(ioDispatcher)
                    .build()
                dBankProDatabaseInstance = instance
                instance
            }
    }
}