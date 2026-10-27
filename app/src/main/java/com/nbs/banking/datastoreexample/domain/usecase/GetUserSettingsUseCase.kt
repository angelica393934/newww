package com.nbs.banking.datastoreexample.domain.usecase

import com.nbs.banking.datastoreexample.domain.model.UserSettings
import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import javax.inject.Inject

interface GetUserSettingsUseCase {
    suspend operator fun invoke(): UserSettings
}

class GetUserSettingsUseCaseImpl @Inject constructor(
    private val repository: UserSettingsRepository
) : GetUserSettingsUseCase {
    override suspend operator fun invoke(): UserSettings = repository.getUserSettings()
}
