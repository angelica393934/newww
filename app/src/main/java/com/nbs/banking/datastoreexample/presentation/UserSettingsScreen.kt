package com.nbs.banking.datastoreexample.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nbs.banking.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension
import com.nbs.banking.datastoreexample.domain.model.SecureData
import com.nbs.banking.datastoreexample.domain.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsScreen(
    state: UserSettingsState,
    onIntent: (UserSettingsIntent) -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_datastore_sample)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimension.SIZE_16)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_16)
        ) {
            // ─── Current DataStore State Summary ─────────────────────────────
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Dimension.SIZE_16)) {
                    Text(stringResource(R.string.label_stored_preferences), style = Bsb.Font.heading3)
                    Spacer(modifier = Modifier.height(Dimension.SIZE_8))
                    Text("1. String (Username): ${state.settings.username}", style = Bsb.Font.text1)
                    Text("2. Boolean (Dark Mode): ${state.settings.isDarkMode}", style = Bsb.Font.text1)
                    Text("3. Int (Notifications): ${state.settings.notificationCount}", style = Bsb.Font.text1)
                    Text("4. Long (Last Login): ${state.settings.lastLoginTime}", style = Bsb.Font.text1)
                    Text("5. Float (Font Scale): ${state.settings.fontScale}", style = Bsb.Font.text1)
                    Text("6. Double (Balance): $${state.settings.balance}", style = Bsb.Font.text1)
                    Text("7. UserProfile (Object): nickname=${state.settings.profile.nickname}, role=${state.settings.profile.role}", style = Bsb.Font.text1)
                    Text("8. SecureData (Encrypted): token=${state.settings.secureData.secureToken}, pin=${state.settings.secureData.secretPin}", style = Bsb.Font.text1)
                }
            }

            // ─── String Type Sample ──────────────────────────────────────────
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text(stringResource(R.string.hint_enter_username)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onIntent(UserSettingsIntent.UpdateUsername(textInput)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_save_string))
            }

            // ─── Boolean Type Sample ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.label_dark_mode))
                Switch(
                    checked = state.settings.isDarkMode,
                    onCheckedChange = { onIntent(UserSettingsIntent.ToggleDarkMode(it)) }
                )
            }

            // ─── Int Type Sample ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.label_notification_count, state.settings.notificationCount))
                Button(onClick = { onIntent(UserSettingsIntent.UpdateNotificationCount(state.settings.notificationCount + 1)) }) {
                    Text("+1 Int")
                }
            }

            // ─── Long Type Sample ────────────────────────────────────────────
            Button(
                onClick = { onIntent(UserSettingsIntent.UpdateLastLoginTime(System.currentTimeMillis())) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_save_time))
            }

            // ─── Float Type Sample ───────────────────────────────────────────
            Text(stringResource(R.string.label_font_scale, state.settings.fontScale.toString()))
            Slider(
                value = state.settings.fontScale,
                onValueChange = { onIntent(UserSettingsIntent.UpdateFontScale(it)) },
                valueRange = 0.5f..2.0f
            )

            // ─── Double Type Sample ──────────────────────────────────────────
            Button(
                onClick = { onIntent(UserSettingsIntent.UpdateBalance(state.settings.balance + 100.50)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_add_balance))
            }

            // ─── Custom Object Type Sample (UserProfile) ─────────────────────
            var nicknameInput by remember { mutableStateOf(state.settings.profile.nickname) }
            var roleInput by remember { mutableStateOf(state.settings.profile.role) }

            OutlinedTextField(
                value = nicknameInput,
                onValueChange = { nicknameInput = it },
                label = { Text(stringResource(R.string.hint_profile_nickname)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = roleInput,
                onValueChange = { roleInput = it },
                label = { Text(stringResource(R.string.hint_profile_role)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onIntent(UserSettingsIntent.UpdateProfile(UserProfile(nickname = nicknameInput, role = roleInput))) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_save_profile))
            }

            // ─── Secured Data Sample (SecuredPreferenceDataStore + Tink) ───────
            var tokenInput by remember { mutableStateOf(state.settings.secureData.secureToken) }
            var pinInput by remember { mutableStateOf(state.settings.secureData.secretPin) }

            OutlinedTextField(
                value = tokenInput,
                onValueChange = { tokenInput = it },
                label = { Text(stringResource(R.string.hint_secure_token)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pinInput,
                onValueChange = { pinInput = it },
                label = { Text(stringResource(R.string.hint_secret_pin)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onIntent(UserSettingsIntent.UpdateSecureData(SecureData(secureToken = tokenInput, secretPin = pinInput))) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_save_secure_data))
            }

            // ─── Eager Read Sample ───────────────────────────────────────────
            Button(
                onClick = { onIntent(UserSettingsIntent.ReadEagerSettings) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_test_eager_read))
            }
            Text(state.eagerReadSummary, style = Bsb.Font.text1)

            // ─── Suspend Read Sample ─────────────────────────────────────────
            Button(
                onClick = { onIntent(UserSettingsIntent.ReadSuspendSettings) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_test_suspend_read))
            }
            Text(state.suspendReadSummary, style = Bsb.Font.text1)

            // ─── Clear DataStore ─────────────────────────────────────────────
            Button(
                onClick = { onIntent(UserSettingsIntent.ClearAllSettings) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.action_clear_datastore))
            }
        }
    }
}
