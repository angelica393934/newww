package com.nbs.banking.note.data

import app.cash.turbine.test
import com.nbs.banking.coredatabase.entitiy.SampleEntity
import com.nbs.banking.note.data.local.SampleLocalDataSource
import com.nbs.banking.note.domain.model.SampleNote
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Clock

class SampleRepositoryImplTest {
    private val localDataSource: SampleLocalDataSource = mockk()
    private lateinit var repository: SampleRepositoryImpl

    private val now = Clock.System.now()
    private val sampleEntity = SampleEntity(id = 1L, note = "Repository Test", createdAt = now)
    private val sampleNote = SampleNote(id = 1L, note = "Repository Test", createdAt = now)

    @Before
    fun setUp() {
        repository = SampleRepositoryImpl(localDataSource)
    }

    @Test
    fun `when getAll called then maps local entities to domain models`() = runTest {
        // Given
        every { localDataSource.getAll() } returns flowOf(listOf(sampleEntity))

        // When
        val flow = repository.getAll()

        // Then
        flow.test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals(sampleNote, notes.first())
            awaitComplete()
        }
    }

    @Test
    fun `when insert called then maps note to entity and delegates to localDataSource`() = runTest {
        // Given
        coEvery { localDataSource.insert(sampleEntity) } returns 101L

        // When
        val result = repository.insert(sampleNote)

        // Then
        assertEquals(101L, result)
    }

    @Test
    fun `when delete called then delegates to localDataSource`() = runTest {
        // Given
        coEvery { localDataSource.delete(1L) } returns Unit

        // When
        repository.delete(1L)

        // Then
        coVerify { localDataSource.delete(1L) }
    }
}
