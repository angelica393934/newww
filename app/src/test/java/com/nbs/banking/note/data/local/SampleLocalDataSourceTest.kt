package com.nbs.banking.note.data.local

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import app.cash.turbine.test
import com.nbs.banking.core.exceptions.DatabaseConstraintException
import com.nbs.banking.core.exceptions.DatabaseException
import com.nbs.banking.core.exceptions.DatabaseFullException
import com.nbs.banking.coredatabase.dao.SampleDao
import com.nbs.banking.coredatabase.entitiy.SampleEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

class SampleLocalDataSourceTest {

    private val sampleDao: SampleDao = mockk()
    private lateinit var localDataSource: SampleLocalDataSourceImpl

    private val now = Clock.System.now()
    private val sampleEntity = SampleEntity(id = 1L, note = "Local DataSource Test", createdAt = now)

    @Before
    fun setUp() {
        localDataSource = SampleLocalDataSourceImpl(sampleDao)
    }

    @Test
    fun `when getAll called then returns flow of entities from dao`() = runTest {
        // Given
        every { sampleDao.getAll() } returns flowOf(listOf(sampleEntity))

        // When
        val resultFlow = localDataSource.getAll()

        // Then
        resultFlow.test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals(sampleEntity, list.first())
            awaitComplete()
        }
    }

    @Test
    fun `given dao throws SQLiteException when getAll collected then emits DatabaseException`() = runTest {
        // given
        val originalException = SQLiteException("Database error")
        every { sampleDao.getAll() } returns flow { throw originalException }

        // when
        val resultFlow = localDataSource.getAll()

        // then
        resultFlow.test {
            val error = awaitError()
            assertTrue(error is DatabaseException)
            assertEquals(originalException, error.cause)
        }
    }

    // endregion

    // region insert

    @Test
    fun `given dao insert succeeds when insert called then delegates and returns row id`() = runTest {
        // given
        coEvery { sampleDao.insert(sampleEntity) } returns 101L

        // when
        val result = localDataSource.insert(sampleEntity)

        // then
        assertEquals(101L, result)
        coVerify(exactly = 1) { sampleDao.insert(sampleEntity) }
    }

    @Test
    fun `given dao throws SQLiteConstraintException when insert called then throws DatabaseConstraintException`() = runTest {
        // given
        val originalException = SQLiteConstraintException("UNIQUE constraint failed")
        coEvery { sampleDao.insert(sampleEntity) } throws originalException

        // when
        try {
            localDataSource.insert(sampleEntity)
            fail("Expected DatabaseConstraintException")
        } catch (e: DatabaseConstraintException) {
            // then
            assertEquals(originalException, e.cause)
        }
    }

    @Test
    fun `given dao throws SQLiteFullException when insert called then throws DatabaseFullException`() = runTest {
        // given
        val originalException = SQLiteFullException("Database is full")
        coEvery { sampleDao.insert(sampleEntity) } throws originalException

        // when
        try {
            localDataSource.insert(sampleEntity)
            fail("Expected DatabaseFullException")
        } catch (e: DatabaseFullException) {
            // then
            assertEquals(originalException, e.cause)
        }
    }

    @Test
    fun `given CancellationException when insert called then rethrows without wrapping`() = runTest {
        // given
        val originalException = CancellationException("Cancelled")
        coEvery { sampleDao.insert(sampleEntity) } throws originalException

        // when
        try {
            localDataSource.insert(sampleEntity)
            fail("Expected CancellationException")
        } catch (e: CancellationException) {
            // then — must be same instance, not wrapped
            assertSame(originalException, e)
        }
    }

    // endregion

    // region delete

    @Test
    fun `given dao delete succeeds when delete called then delegates to dao`() = runTest {
        // given
        coEvery { sampleDao.delete(1L) } returns Unit

        // when
        localDataSource.delete(1L)

        // then
        coVerify(exactly = 1) { sampleDao.delete(1L) }
    }

    @Test
    fun `given dao throws SQLiteException when delete called then throws DatabaseException`() = runTest {
        // given
        val originalException = SQLiteException("Disk I/O error")
        coEvery { sampleDao.delete(1L) } throws originalException

        // when
        try {
            localDataSource.delete(1L)
            fail("Expected DatabaseException")
        } catch (e: DatabaseException) {
            // then
            assertEquals(originalException, e.cause)
        }
    }

    // endregion
}
