package com.nbs.banking.datastoreexample.presentation

import com.nbs.banking.datastoreexample.domain.model.SecureData
import com.nbs.banking.datastoreexample.domain.model.UserProfile
import com.nbs.banking.datastoreexample.domain.model.UserSettings

sealed interface UserSettingsIntent {
    data class UpdateUsername(val username: String) : UserSettingsIntent
    data class ToggleDarkMode(val enabled: Boolean) : UserSettingsIntent
    data class UpdateNotificationCount(val count: Int) : UserSettingsIntent
    data class UpdateLastLoginTime(val time: Long) : UserSettingsIntent
    data class UpdateFontScale(val scale: Float) : UserSettingsIntent
    data class UpdateBalance(val balance: Double) : UserSettingsIntent
    data class UpdateProfile(val profile: UserProfile) : UserSettingsIntent
    data class UpdateSecureData(val secureData: SecureData) : UserSettingsIntent
    data object ReadEagerSettings : UserSettingsIntent
    data object ReadSuspendSettings : UserSettingsIntent
    data object ClearAllSettings : UserSettingsIntent
}

sealed interface UserSettingsEvent {
    data class SettingsUpdated(val settings: UserSettings) : UserSettingsEvent
    data class EagerReadUpdated(val summary: String) : UserSettingsEvent
    data class SuspendReadUpdated(val summary: String) : UserSettingsEvent
}

data class UserSettingsState(
    val settings: UserSettings = UserSettings(),
    val eagerReadSummary: String = "Eager Read: Not Triggered",
    val suspendReadSummary: String = "Suspend Read: Not Triggered"
)

sealed interface UserSettingsEffect {
    data class ShowToast(val message: String) : UserSettingsEffect
}
