package com.nbs.banking.datastoreexample.presentation

import androidx.lifecycle.viewModelScope
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.datastoreexample.domain.model.SaveUserSettingsParam
import com.nbs.banking.datastoreexample.domain.usecase.ClearUserSettingsUseCase
import com.nbs.banking.datastoreexample.domain.usecase.GetUserSettingsEagerUseCase
import com.nbs.banking.datastoreexample.domain.usecase.GetUserSettingsUseCase
import com.nbs.banking.datastoreexample.domain.usecase.ObserveUserSettingsUseCase
import com.nbs.banking.datastoreexample.domain.usecase.SaveUserSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val getUserSettingsEagerUseCase: GetUserSettingsEagerUseCase,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    private val saveUserSettingUseCase: SaveUserSettingUseCase,
    private val clearUserSettingsUseCase: ClearUserSettingsUseCase
) : BaseMviViewModel<UserSettingsIntent, UserSettingsEvent, UserSettingsState, UserSettingsEffect>(
    UserSettingsState()
) {

    init {
        observeSettings()
    }

    private fun observeSettings() {
        observeUserSettingsUseCase()
            .onEach { updated ->
                sendEvent(UserSettingsEvent.SettingsUpdated(updated))
            }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: UserSettingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is UserSettingsIntent.UpdateUsername -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.Username(intent.username))
                }
                is UserSettingsIntent.ToggleDarkMode -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.DarkMode(intent.enabled))
                }
                is UserSettingsIntent.UpdateNotificationCount -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.NotificationCount(intent.count))
                }
                is UserSettingsIntent.UpdateLastLoginTime -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.LastLoginTime(intent.time))
                }
                is UserSettingsIntent.UpdateFontScale -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.FontScale(intent.scale))
                }
                is UserSettingsIntent.UpdateBalance -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.AccountBalance(intent.balance))
                }
                is UserSettingsIntent.UpdateProfile -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.Profile(intent.profile))
                }
                is UserSettingsIntent.UpdateSecureData -> {
                    saveUserSettingUseCase(SaveUserSettingsParam.SecureDataParam(intent.secureData))
                }
                is UserSettingsIntent.ReadEagerSettings -> {
                    val eager = getUserSettingsEagerUseCase()
                    sendEvent(UserSettingsEvent.EagerReadUpdated("Eager Read: $eager"))
                }
                is UserSettingsIntent.ReadSuspendSettings -> {
                    val suspendResult = getUserSettingsUseCase()
                    Timber.tag("UserSettings").d("Suspend Read: $suspendResult")
                    sendEvent(UserSettingsEvent.SuspendReadUpdated("Suspend Read: $suspendResult"))
                }
                is UserSettingsIntent.ClearAllSettings -> {
                    clearUserSettingsUseCase()
                    sendEffect(UserSettingsEffect.ShowToast("DataStore cleared successfully"))
                }
            }
        }
    }

    override fun reduce(currentState: UserSettingsState, event: UserSettingsEvent): UserSettingsState =
        when (event) {
            is UserSettingsEvent.SettingsUpdated -> currentState.copy(settings = event.settings)
            is UserSettingsEvent.EagerReadUpdated -> currentState.copy(eagerReadSummary = event.summary)
            is UserSettingsEvent.SuspendReadUpdated -> currentState.copy(suspendReadSummary = event.summary)
        }
}
