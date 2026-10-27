package com.nbs.banking.datastoreexample.domain.model

sealed class SaveUserSettingsParam {
    data class Username(val username: String) : SaveUserSettingsParam()
    data class DarkMode(val isDarkMode: Boolean) : SaveUserSettingsParam()
    data class NotificationCount(val count: Int) : SaveUserSettingsParam()
    data class LastLoginTime(val time: Long) : SaveUserSettingsParam()
    data class FontScale(val scale: Float) : SaveUserSettingsParam()
    data class AccountBalance(val balance: Double) : SaveUserSettingsParam()
    data class Profile(val profile: UserProfile) : SaveUserSettingsParam()
    data class SecureDataParam(val secureData: SecureData) : SaveUserSettingsParam()
}