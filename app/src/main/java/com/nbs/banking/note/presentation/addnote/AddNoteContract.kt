package com.nbs.banking.note.presentation.addnote

import com.nbs.banking.core.extensions.emptyString
import com.nbs.banking.core.state.ScreenState

sealed interface AddNoteIntent {
    data class OnNoteTextChanged(val text: String) : AddNoteIntent
    data object SaveNote : AddNoteIntent
}

sealed interface AddNoteEvent {
    data class NoteTextChanged(val text: String) : AddNoteEvent
    data object Saving : AddNoteEvent
    data class SaveSuccess(val id: Long) : AddNoteEvent
    data class SaveError(val error: Throwable) : AddNoteEvent
}

data class AddNoteState(
    val noteText: String = emptyString(),
    val saveState: ScreenState<Long> = ScreenState.default(),
) {
    fun saving() = copy(saveState = ScreenState.loading())
    fun saveSuccess(id: Long) = copy(saveState = ScreenState.success(id))
    fun saveError(error: Throwable) = copy(saveState = ScreenState.error(error, error.message.orEmpty()))
}

sealed interface AddNoteEffect {
    data class ShowSnackBar(val message: String) : AddNoteEffect
    data class NavigateBack(val message: String) : AddNoteEffect
}
