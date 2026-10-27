package com.nbs.banking.coredatabase.utils

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import com.nbs.banking.core.exceptions.DatabaseConstraintException
import com.nbs.banking.core.exceptions.DatabaseException
import com.nbs.banking.core.exceptions.DatabaseFullException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeDbCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: SQLiteConstraintException) {
        throw DatabaseConstraintException(e, e.message.orEmpty())
    } catch (e: SQLiteFullException) {
        throw DatabaseFullException(e, e.message.orEmpty())
    } catch (e: SQLiteException) {
        throw DatabaseException(e, e.message.orEmpty())
    }
}

fun <T> Flow<T>.catchDbExceptions(): Flow<T> =
    catch { e ->
        when (e) {
            is CancellationException -> throw e
            is SQLiteConstraintException -> throw DatabaseConstraintException(
                e,
                e.message.orEmpty()
            )

            is SQLiteFullException -> throw DatabaseFullException(e, e.message.orEmpty())
            is SQLiteException -> throw DatabaseException(e, e.message.orEmpty())
            else -> throw e
        }
    }