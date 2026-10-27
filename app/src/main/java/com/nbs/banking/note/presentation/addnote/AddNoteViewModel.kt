package com.nbs.banking.note.presentation.addnote

import androidx.lifecycle.viewModelScope
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.core.extensions.startMviSuspend
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.note.domain.model.SampleNote
import com.nbs.banking.note.domain.usecase.InsertNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Clock

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val insertNoteUseCase: InsertNoteUseCase
) : BaseMviViewModel<AddNoteIntent, AddNoteEvent, AddNoteState, AddNoteEffect>(
    AddNoteState(saveState = ScreenState.default())
) {

    override fun onIntent(intent: AddNoteIntent) {
        when (intent) {
            is AddNoteIntent.OnNoteTextChanged -> sendEvent(AddNoteEvent.NoteTextChanged(intent.text))
            AddNoteIntent.SaveNote -> saveNote()
        }
    }

    override fun reduce(
        currentState: AddNoteState,
        event: AddNoteEvent
    ): AddNoteState =
        when (event) {
            is AddNoteEvent.NoteTextChanged -> currentState.copy(noteText = event.text)
            AddNoteEvent.Saving -> currentState.saving()
            is AddNoteEvent.SaveSuccess -> currentState.saveSuccess(event.id)
            is AddNoteEvent.SaveError -> currentState.saveError(event.error)
        }

    private fun saveNote() {
        val text = state.value.noteText.trim()
        if (text.isEmpty()) {
            sendEffect(AddNoteEffect.ShowSnackBar("Note cannot be empty"))
            return
        }

        viewModelScope.launch {
            startMviSuspend(
                onLoading = { sendEvent(AddNoteEvent.Saving) },
                onError = { error ->
                    sendEvent(AddNoteEvent.SaveError(error))
                    sendEffect(AddNoteEffect.ShowSnackBar(error.message.orEmpty()))
                },
                onSuccess = { newId ->
                    sendEvent(AddNoteEvent.SaveSuccess(newId))
                    sendEffect(AddNoteEffect.NavigateBack("Note saved successfully"))
                }
            ) {
                insertNoteUseCase(
                    SampleNote(
                        note = text,
                        createdAt = Clock.System.now()
                    )
                )
            }
        }
    }
}
