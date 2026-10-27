package com.nbs.banking.datastoreexample.domain.usecase

import com.nbs.banking.datastoreexample.domain.model.UserSettings
import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveUserSettingsUseCase {
    operator fun invoke(): Flow<UserSettings>
}

class ObserveUserSettingsUseCaseImpl @Inject constructor(
    private val repository: UserSettingsRepository
) : ObserveUserSettingsUseCase {
    override operator fun invoke(): Flow<UserSettings> = repository.observeUserSettings()
}
