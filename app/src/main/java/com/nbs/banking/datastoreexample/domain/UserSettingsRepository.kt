package com.nbs.banking.datastoreexample.domain

import com.nbs.banking.datastoreexample.domain.model.SecureData
import com.nbs.banking.datastoreexample.domain.model.UserProfile
import com.nbs.banking.datastoreexample.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun observeUserSettings(): Flow<UserSettings>
    fun getUserSettingsEager(): UserSettings
    suspend fun getUserSettings(): UserSettings
    suspend fun saveUsername(username: String)
    suspend fun saveDarkMode(isDarkMode: Boolean)
    suspend fun saveNotificationCount(count: Int)
    suspend fun saveLastLoginTime(time: Long)
    suspend fun saveFontScale(scale: Float)
    suspend fun saveAccountBalance(balance: Double)
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun saveSecureData(secureData: SecureData)
    suspend fun removeUserSettings()
}
