package com.nbs.banking.note.presentation.notelist

import androidx.lifecycle.viewModelScope
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.core.extensions.catchMviException
import com.nbs.banking.core.extensions.startMviSuspend
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.note.domain.usecase.DeleteNoteUseCase
import com.nbs.banking.note.domain.usecase.GetAllSampleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.microseconds

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getAllSampleUseCase: GetAllSampleUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : BaseMviViewModel<NoteListIntent, NoteListEvent, NoteListState, NoteListEffect>(
    NoteListState(ScreenState.default())
) {

    init {
        onIntent(NoteListIntent.LoadNotes)
    }

    override fun onIntent(intent: NoteListIntent) {
        when (intent) {
            NoteListIntent.LoadNotes -> fetchNoteList()
            is NoteListIntent.DeleteNote -> deleteNote(intent.id)
        }
    }

    override fun reduce(
        currentState: NoteListState,
        event: NoteListEvent
    ): NoteListState =
        when (event) {
            NoteListEvent.Loading -> currentState.loading()
            is NoteListEvent.Loaded -> currentState.success(event.notes)
            is NoteListEvent.Error -> currentState.error(event.throwable)

            NoteListEvent.Deleting -> currentState.deleting()
            NoteListEvent.DeleteSuccess -> currentState.deleteSuccess()
            is NoteListEvent.DeleteError -> currentState.deleteError(event.throwable)
        }


    private fun fetchNoteList() {
        viewModelScope.launch {
            getAllSampleUseCase()
                .onEach { delay(1000.microseconds) }
                .onStart { sendEvent(NoteListEvent.Loading) }
                .catchMviException { error -> sendEvent(NoteListEvent.Error(error)) }
                .collect { notes -> sendEvent(NoteListEvent.Loaded(notes)) }
        }
    }

    private fun deleteNote(id: Long) {
        viewModelScope.launch {
            startMviSuspend(
                onLoading = { sendEvent(NoteListEvent.Deleting) },
                onError = { error ->
                    sendEvent(NoteListEvent.DeleteError(error))
                    sendEffect(NoteListEffect.ShowSnackBar(error.message.orEmpty()))
                },
                onSuccess = {
                    sendEvent(NoteListEvent.DeleteSuccess)
                    sendEffect(NoteListEffect.ShowSnackBar("Note deleted successfully"))
                }) {
                delay(200.microseconds)
                deleteNoteUseCase(id)
            }
        }
    }
}