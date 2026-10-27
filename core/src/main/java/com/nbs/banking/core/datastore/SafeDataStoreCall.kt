package com.nbs.banking.core.datastore

import com.nbs.banking.core.exceptions.DataStoreReadException
import com.nbs.banking.core.exceptions.DataStoreWriteException
import com.nbs.banking.core.exceptions.EncryptDataException
import com.nbs.banking.core.exceptions.EncryptionException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

fun <T> Flow<T>.catchDataStoreExceptions(): Flow<T> =
    catch { e ->
        when (e) {
            is CancellationException -> throw e
            is EncryptionException -> throw e
            is IOException -> throw DataStoreReadException(e, e.message.orEmpty())
            else -> throw e
        }
    }

suspend fun <T> safeDataStoreWrite(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: EncryptDataException) {
        throw e
    } catch (e: IOException) {
        throw DataStoreWriteException(e, e.message.orEmpty())
    }
}