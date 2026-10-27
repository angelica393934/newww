package com.nbs.banking.note.presentation.addnote

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import com.nbs.banking.R
import com.nbs.banking.components.LoadingDialog
import com.nbs.banking.core.state.isLoading
import com.nbs.banking.core.utils.SafeClickWrapper
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun AddNoteScreen(
    state: AddNoteState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (AddNoteIntent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    fun dismissKeyboard() {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    if (state.saveState.isLoading()) {
        LoadingDialog(message = stringResource(R.string.message_saving_note))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimension.SIZE_16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_add_note),
                style = Bsb.Font.heading3,
                color = Bsb.Color.baseColor.primary1
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_16))

            OutlinedTextField(
                value = state.noteText,
                onValueChange = { onIntent(AddNoteIntent.OnNoteTextChanged(it)) },
                label = { Text(stringResource(R.string.hint_note_content)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.saveState.isLoading()
            )

            Spacer(modifier = Modifier.height(Dimension.SIZE_16))

            SafeClickWrapper({
                dismissKeyboard()
                onIntent(AddNoteIntent.SaveNote)
            }) {
                Button(
                    onClick = it,
                    enabled = !state.saveState.isLoading(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.saveState.isLoading()) stringResource(R.string.action_saving) else stringResource(R.string.action_save_note))
                }
            }

            Spacer(modifier = Modifier.height(Dimension.SIZE_8))

            SafeClickWrapper({
                dismissKeyboard()
                onBack()
            }) {
                Button(
                    onClick = it,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    }
}
