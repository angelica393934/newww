package com.nbs.banking.note.presentation

import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.core.state.isLoading
import com.nbs.banking.note.domain.usecase.InsertNoteUseCase
import com.nbs.banking.note.presentation.addnote.AddNoteEffect
import com.nbs.banking.note.presentation.addnote.AddNoteEvent
import com.nbs.banking.note.presentation.addnote.AddNoteIntent
import com.nbs.banking.note.presentation.addnote.AddNoteState
import com.nbs.banking.note.presentation.addnote.AddNoteViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val insertNoteUseCase: InsertNoteUseCase = mockk()

    private lateinit var viewModel: AddNoteViewModel

    private fun createViewModel() {
        viewModel = AddNoteViewModel(insertNoteUseCase)
    }

    // ─── OnNoteTextChanged ────────────────────────────────────────────────────

    @Test
    fun `when OnNoteTextChanged intent is triggered then updates noteText state`() = runTest {
        // Given
        createViewModel()

        // When
        viewModel.onIntent(
            AddNoteIntent.OnNoteTextChanged("New Note Content")
        )

        // Then
        assertEquals(
            "New Note Content",
            viewModel.state.value.noteText
        )
    }

    // ─── SaveNote ─────────────────────────────────────────────────────────────

    @Test
    fun `when SaveNote intent is triggered with empty note then shows empty snackbar`() = runTest {
        // Given
        createViewModel()

        // When
        viewModel.onIntent(AddNoteIntent.SaveNote)

        // Then
        viewModel.effect.test {
            val effect = awaitItem() as AddNoteEffect.ShowSnackBar
            assertEquals("Note cannot be empty", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when SaveNote intent succeeds then updates saveState and emits navigate back effect`() = runTest {
        // Given
        coEvery { insertNoteUseCase(any()) } coAnswers {
            delay(100.milliseconds)
            101L
        }

        createViewModel()

        // When
        viewModel.onIntent(AddNoteIntent.OnNoteTextChanged("My Note Content"))

        viewModel.state.test {
            skipItems(1) // skip initial state

            viewModel.onIntent(AddNoteIntent.SaveNote)

            val savingState = awaitItem()
            assertTrue(savingState.saveState.isLoading())

            val successState = awaitItem()
            assertEquals(ScreenState.success(101L), successState.saveState)

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            val effect = awaitItem() as AddNoteEffect.NavigateBack
            assertEquals("Note saved successfully", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when SaveNote intent fails then updates saveState and shows error snackbar`() = runTest {
        // Given
        val exception = RuntimeException("Database Error")

        coEvery { insertNoteUseCase(any()) } coAnswers {
            delay(100.milliseconds)
            throw exception
        }

        createViewModel()

        // When
        viewModel.onIntent(AddNoteIntent.OnNoteTextChanged("My Note Content"))

        viewModel.state.test {
            skipItems(1) // skip initial state

            viewModel.onIntent(AddNoteIntent.SaveNote)

            val savingState = awaitItem()
            assertTrue(savingState.saveState.isLoading())

            val errorState = awaitItem()
            assertEquals(
                ScreenState.error<Long>(exception, "Database Error"),
                errorState.saveState
            )

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            val effect = awaitItem() as AddNoteEffect.ShowSnackBar
            assertEquals("Database Error", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Reducer Tests ────────────────────────────────────────────────────────

    @Test
    fun `reduce NoteTextChanged returns updated noteText state`() = runTest {
        // Given
        createViewModel()

        val initialState = AddNoteState()

        // When
        val result = viewModel.reduce(
            initialState,
            AddNoteEvent.NoteTextChanged("Test")
        )

        // Then
        assertEquals(
            "Test",
            result.noteText
        )
    }

    @Test
    fun `reduce Saving returns saving state`() = runTest {
        // Given
        createViewModel()

        val initialState = AddNoteState()

        // When
        val result = viewModel.reduce(
            initialState,
            AddNoteEvent.Saving
        )

        // Then
        assertEquals(
            ScreenState.loading<Long>(),
            result.saveState
        )
    }

    @Test
    fun `reduce SaveSuccess returns save success state`() = runTest {
        // Given
        createViewModel()

        val initialState = AddNoteState()

        // When
        val result = viewModel.reduce(
            initialState,
            AddNoteEvent.SaveSuccess(100L)
        )

        // Then
        assertEquals(
            ScreenState.success(100L),
            result.saveState
        )
    }

    @Test
    fun `reduce SaveError returns save error state`() = runTest {
        // Given
        createViewModel()

        val error = RuntimeException("Error")
        val initialState = AddNoteState()

        // When
        val result = viewModel.reduce(
            initialState,
            AddNoteEvent.SaveError(error)
        )

        // Then
        assertEquals(
            ScreenState.error<Long>(error, "Error"),
            result.saveState
        )
    }
}
