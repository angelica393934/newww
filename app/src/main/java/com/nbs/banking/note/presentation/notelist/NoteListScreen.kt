package com.nbs.banking.note.presentation.notelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nbs.banking.R
import com.nbs.banking.core.state.Render
import com.nbs.banking.core.utils.SafeClickWrapper
import com.nbs.banking.note.domain.model.SampleNote
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun NoteListScreen(
    state: NoteListState,
    snackbarHostState: SnackbarHostState,
    onAddNoteClicked: () -> Unit,
    onIntent: (NoteListIntent) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            SafeClickWrapper({ onAddNoteClicked() }) {
                FloatingActionButton(
                    onClick = it
                ) {
                    Text(
                        text = "+",
                        style = Bsb.Font.heading2
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            state.notesState.Render(
                onLoading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.message_loading_notes), style = Bsb.Font.text1)
                    }
                },
                onSuccess = { notes ->
                    if (notes.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(R.string.message_no_notes), style = Bsb.Font.text1)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Dimension.SIZE_16),
                            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_8)
                        ) {
                            items(
                                items = notes,
                                key = { it.id }
                            ) { note ->
                                NoteItem(
                                    note = note,
                                    onDeleteNote = { onIntent(NoteListIntent.DeleteNote(note.id)) }
                                )
                            }
                        }
                    }
                },
                onError = { _, message ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.error_message, message), style = Bsb.Font.text1)
                    }
                }
            )
        }
    }
}

@Composable
private fun NoteItem(
    note: SampleNote,
    onDeleteNote: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimension.SIZE_16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.note,
                    style = Bsb.Font.text1
                )
                Spacer(modifier = Modifier.height(Dimension.SIZE_4))
                Text(
                    text = note.createdAt.toString(),
                    style = Bsb.Font.caption1
                )
            }
            SafeClickWrapper({ onDeleteNote() }) {
                Button(
                    onClick = it,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            }
        }
    }
}
