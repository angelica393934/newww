package com.nbs.banking.note.presentation.notelist

import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.note.domain.model.SampleNote

sealed interface NoteListIntent {
    data object LoadNotes : NoteListIntent
    data class DeleteNote(val id: Long) : NoteListIntent
}

sealed interface NoteListEvent {
    // List Events
    data object Loading : NoteListEvent
    data class Loaded(val notes: List<SampleNote>) : NoteListEvent
    data class Error(val throwable: Throwable) : NoteListEvent

    // Delete Events
    data object Deleting : NoteListEvent
    data object DeleteSuccess : NoteListEvent
    data class DeleteError(val throwable: Throwable) : NoteListEvent
}

data class NoteListState(
    val notesState: ScreenState<List<SampleNote>> = ScreenState.default(),
    val deleteState: ScreenState<Unit> = ScreenState.default(),
) {
    // List State Helpers
    fun loading() = copy(notesState = ScreenState.loading())
    fun success(notes: List<SampleNote>) = copy(notesState = ScreenState.success(notes))
    fun error(throwable: Throwable) = copy(notesState = ScreenState.error(throwable, throwable.message.orEmpty()))

    // Delete State Helpers
    fun deleting() = copy(deleteState = ScreenState.loading())
    fun deleteSuccess() = copy(deleteState = ScreenState.success(Unit))
    fun deleteError(throwable: Throwable) = copy(deleteState = ScreenState.error(throwable, throwable.message.orEmpty()))
}

sealed interface NoteListEffect {
    data class ShowSnackBar(val message: String) : NoteListEffect
}