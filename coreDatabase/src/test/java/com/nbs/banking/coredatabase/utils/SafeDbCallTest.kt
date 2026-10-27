package com.nbs.banking.coredatabase.utils

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import app.cash.turbine.test
import com.nbs.banking.core.exceptions.DatabaseConstraintException
import com.nbs.banking.core.exceptions.DatabaseException
import com.nbs.banking.core.exceptions.DatabaseFullException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeDbCallTest {

    @Test
    fun `when block succeeds then safeDbCall returns result`() = runTest {
        val result = safeDbCall { "DB Success" }
        assertEquals("DB Success", result)
    }

    @Test(expected = DatabaseConstraintException::class)
    fun `when block throws SQLiteConstraintException then safeDbCall throws DatabaseConstraintException`() = runTest {
        safeDbCall<String> { throw SQLiteConstraintException("Constraint error") }
    }

    @Test(expected = DatabaseFullException::class)
    fun `when block throws SQLiteFullException then safeDbCall throws DatabaseFullException`() = runTest {
        safeDbCall<String> { throw SQLiteFullException("Database disk full") }
    }

    @Test(expected = DatabaseException::class)
    fun `when block throws SQLiteException then safeDbCall throws DatabaseException`() = runTest {
        safeDbCall<String> { throw SQLiteException("Generic DB error") }
    }

    @Test
    fun `when flow emits successfully then catchDbExceptions passes items`() = runTest {
        flowOf(1, 2, 3).catchDbExceptions().test {
            assertEquals(1, awaitItem())
            assertEquals(2, awaitItem())
            assertEquals(3, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when flow throws SQLiteException then catchDbExceptions converts to DatabaseException`() = runTest {
        flow<Int> { throw SQLiteException("Flow DB error") }.catchDbExceptions().test {
            val error = awaitError()
            assertTrue(error is DatabaseException)
        }
    }
}
