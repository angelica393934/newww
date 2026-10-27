package com.nbs.banking.datastoreexample.domain.usecase

import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import com.nbs.banking.datastoreexample.domain.model.SaveUserSettingsParam
import javax.inject.Inject

interface SaveUserSettingUseCase {
    suspend operator fun invoke(param: SaveUserSettingsParam)
}

class SaveUserSettingUseCaseImpl @Inject constructor(
    private val repository: UserSettingsRepository
) : SaveUserSettingUseCase {
    override suspend fun invoke(param: SaveUserSettingsParam) {
        when (param) {
            is SaveUserSettingsParam.Username -> repository.saveUsername(param.username)
            is SaveUserSettingsParam.DarkMode -> repository.saveDarkMode(param.isDarkMode)
            is SaveUserSettingsParam.NotificationCount -> repository.saveNotificationCount(param.count)
            is SaveUserSettingsParam.LastLoginTime -> repository.saveLastLoginTime(param.time)
            is SaveUserSettingsParam.FontScale -> repository.saveFontScale(param.scale)
            is SaveUserSettingsParam.AccountBalance -> repository.saveAccountBalance(param.balance)
            is SaveUserSettingsParam.Profile -> repository.saveUserProfile(param.profile)
            is SaveUserSettingsParam.SecureDataParam -> repository.saveSecureData(param.secureData)
        }
    }
}