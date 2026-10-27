package com.nbs.banking.note.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import com.nbs.banking.navigation.back
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.NoteListDestination
import com.nbs.banking.navigation.goTo
import com.nbs.banking.note.presentation.addnote.AddNoteEffect
import com.nbs.banking.note.presentation.addnote.AddNoteScreen
import com.nbs.banking.note.presentation.addnote.AddNoteViewModel
import com.nbs.banking.note.presentation.notelist.NoteListEffect
import com.nbs.banking.note.presentation.notelist.NoteListScreen
import com.nbs.banking.note.presentation.notelist.NoteListViewModel

fun EntryProviderScope<AppNavKey>.noteNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<NoteListDestination> {
        val viewModel: NoteListViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }

        ResultEffect<String>(resultKey = AddNoteDestination.RESULT_KEY) {
            snackbarHostState.showSnackbar(it)
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is NoteListEffect.ShowSnackBar -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        NoteListScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            onAddNoteClicked = { backStack.goTo(AddNoteDestination) },
            onIntent = viewModel::onIntent
        )
    }

    entry<AddNoteDestination> {
        val viewModel: AddNoteViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val resultBus = LocalResultEventBus.current

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AddNoteEffect.ShowSnackBar ->  snackbarHostState.showSnackbar(effect.message)
                    is AddNoteEffect.NavigateBack -> {
                        resultBus.sendResult(
                            resultKey = AddNoteDestination.RESULT_KEY,
                            result = effect.message
                        )
                        backStack.back()
                    }
                }
            }
        }

        AddNoteScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            onBack = { backStack.back() },
            onIntent = viewModel::onIntent
        )
    }
}
