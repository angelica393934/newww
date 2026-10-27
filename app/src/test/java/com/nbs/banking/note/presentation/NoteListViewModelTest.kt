package com.nbs.banking.note.presentation

import app.cash.turbine.test
import com.nbs.banking.MainCoroutineRule
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.note.domain.model.SampleNote
import com.nbs.banking.note.domain.usecase.DeleteNoteUseCase
import com.nbs.banking.note.domain.usecase.GetAllSampleUseCase
import com.nbs.banking.note.presentation.notelist.NoteListEffect
import com.nbs.banking.note.presentation.notelist.NoteListEvent
import com.nbs.banking.note.presentation.notelist.NoteListIntent
import com.nbs.banking.note.presentation.notelist.NoteListState
import com.nbs.banking.note.presentation.notelist.NoteListViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val getAllSampleUseCase: GetAllSampleUseCase = mockk()
    private val deleteNoteUseCase: DeleteNoteUseCase = mockk()

    private lateinit var viewModel: NoteListViewModel

    private val sampleNotes = listOf(
        SampleNote(id = 1, note = "First Note", createdAt = Clock.System.now()),
        SampleNote(id = 2, note = "Second Note", createdAt = Clock.System.now())
    )

    @Before
    fun setUp() {
        coEvery { getAllSampleUseCase() } returns flowOf()
    }

    private fun createViewModel() {
        viewModel = NoteListViewModel(getAllSampleUseCase, deleteNoteUseCase)
    }

    // ─── LoadNotes ────────────────────────────────────────────────────────────

    @Test
    fun `when init then should load notes successfully`() = runTest {
        // Given
        every { getAllSampleUseCase() } returns flow {
            delay(100.milliseconds)
            emit(sampleNotes)
        }

        // When
        createViewModel()

        // Then
        viewModel.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.notesState is ScreenState.Loading)

            val successState = awaitItem()
            assertEquals(ScreenState.success(sampleNotes), successState.notesState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when fetchNoteList fails then emits error state`() = runTest {
        // Given
        val exception = RuntimeException("Database Error")
        every { getAllSampleUseCase() } returns flow {
            delay(100.milliseconds)
            throw exception
        }

        // When
        createViewModel()

        // Then
        viewModel.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.notesState is ScreenState.Loading)

            val errorState = awaitItem()
            assertEquals(ScreenState.error<List<SampleNote>>(exception, "Database Error"), errorState.notesState)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── DeleteNote ───────────────────────────────────────────────────────────

    @Test
    fun `when DeleteNote intent succeeds then updates deleteState and emits snackbar effect`() = runTest {
        // Given
        coEvery { deleteNoteUseCase(1L) } coAnswers {
            delay(100.milliseconds)
        }
        createViewModel()

        // When / Then
        viewModel.state.test {
            skipItems(1) // skip LoadNotes init state

            // When
            viewModel.onIntent(NoteListIntent.DeleteNote(1L))

            // Then — deleting
            val deletingState = awaitItem()
            assertTrue(deletingState.deleteState is ScreenState.Loading)

            // Then — success
            val successState = awaitItem()
            assertEquals(ScreenState.success(Unit), successState.deleteState)

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            val effect = awaitItem() as NoteListEffect.ShowSnackBar
            assertEquals("Note deleted successfully", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when DeleteNote intent fails then updates error deleteState and emits error snackbar`() = runTest {
        // Given
        val exception = RuntimeException("Delete Error")
        coEvery { deleteNoteUseCase(1L) } coAnswers {
            delay(100.milliseconds)
            throw exception
        }
        createViewModel()

        // When / Then
        viewModel.state.test {
            skipItems(1) // skip LoadNotes init state

            // When
            viewModel.onIntent(NoteListIntent.DeleteNote(1L))

            // Then — deleting
            val deletingState = awaitItem()
            assertTrue(deletingState.deleteState is ScreenState.Loading)

            // Then — error
            val errorState = awaitItem()
            assertEquals(
                ScreenState.error<Unit>(exception, "Delete Error"),
                errorState.deleteState
            )

            cancelAndIgnoreRemainingEvents()
        }

        viewModel.effect.test {
            val effect = awaitItem() as NoteListEffect.ShowSnackBar
            assertEquals("Delete Error", effect.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ─── Reducer Tests ────────────────────────────────────────────────────────

    @Test
    fun `reduce Loading returns loading notesState`() {
        // Given
        createViewModel()
        val initialState = NoteListState()

        // When
        val result = viewModel.reduce(initialState, NoteListEvent.Loading)

        // Then
        assertEquals(ScreenState.loading<List<SampleNote>>(), result.notesState)
    }

    @Test
    fun `reduce Loaded returns success notesState`() {
        // Given
        createViewModel()
        val initialState = NoteListState()

        // When
        val result = viewModel.reduce(initialState, NoteListEvent.Loaded(sampleNotes))

        // Then
        assertEquals(ScreenState.success(sampleNotes), result.notesState)
    }

    @Test
    fun `reduce Error returns error notesState`() {
        // Given
        createViewModel()
        val exception = RuntimeException("Fetch Error")
        val initialState = NoteListState()

        // When
        val result = viewModel.reduce(initialState, NoteListEvent.Error(exception))

        // Then
        assertEquals(ScreenState.error<List<SampleNote>>(exception, "Fetch Error"), result.notesState)
    }

    @Test
    fun `reduce Deleting returns deleting state`() {
        // Given
        createViewModel()
        val initialState = NoteListState()

        // When
        val result = viewModel.reduce(initialState, NoteListEvent.Deleting)

        // Then
        assertEquals(ScreenState.loading<Unit>(), result.deleteState)
    }

    @Test
    fun `reduce DeleteSuccess returns deleteSuccess state`() {
        // Given
        createViewModel()
        val initialState = NoteListState()

        // When
        val result = viewModel.reduce(initialState, NoteListEvent.DeleteSuccess)

        // Then
        assertEquals(ScreenState.success(Unit), result.deleteState)
    }

    @Test
    fun `reduce DeleteError returns deleteError state`() {
        // Given
        createViewModel()
        val exception = RuntimeException("Delete Error")
        val initialState = NoteListState()

        // When
        val result = viewModel.reduce(initialState, NoteListEvent.DeleteError(exception))

        // Then
        assertEquals(ScreenState.error<Unit>(exception, "Delete Error"), result.deleteState)
    }
}
