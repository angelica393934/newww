package com.nbs.banking.note.domain.usecase

import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.note.domain.SampleRepository
import com.nbs.banking.note.domain.model.SampleNote
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Clock

class SampleNoteUseCasesTest {
    private val sampleRepository: SampleRepository = mockk()

    private lateinit var getAllSampleUseCase: GetAllSampleUseCaseImpl
    private lateinit var insertNoteUseCase: InsertNoteUseCaseImpl
    private lateinit var deleteNoteUseCase: DeleteNoteUseCaseImpl

    private val now = Clock.System.now()
    private val sampleNote = SampleNote(id = 1L, note = "UseCase Test", createdAt = now)

    @Before
    fun setUp() {
        getAllSampleUseCase = GetAllSampleUseCaseImpl(sampleRepository)
        insertNoteUseCase = InsertNoteUseCaseImpl(sampleRepository)
        deleteNoteUseCase = DeleteNoteUseCaseImpl(sampleRepository)
    }

    @Test
    fun `when GetAllSampleUseCase invoked then delegates to repository`() = runTest {
        // Given
        every { sampleRepository.getAll() } returns flowOf(listOf(sampleNote))

        // When
        val flow = getAllSampleUseCase()

        // Then
        flow.test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals(sampleNote, notes.first())
            awaitComplete()
        }
    }

    @Test
    fun `when InsertNoteUseCase invoked then delegates to repository`() = runTest {
        // Given
        coEvery { sampleRepository.insert(sampleNote) } returns 101L

        // When
        val result = insertNoteUseCase(sampleNote)

        // Then
        assertEquals(101L, result)
    }

    @Test
    fun `when DeleteNoteUseCase invoked then delegates to repository`() = runTest {
        // Given
        coEvery { sampleRepository.delete(1L) } returns Unit

        // When
        deleteNoteUseCase(1L)

        // Then
        coVerify { sampleRepository.delete(1L) }
    }
}
