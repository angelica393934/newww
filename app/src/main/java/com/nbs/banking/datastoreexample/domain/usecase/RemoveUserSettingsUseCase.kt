package com.nbs.banking.datastoreexample.domain.usecase

import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import javax.inject.Inject

interface ClearUserSettingsUseCase {
    suspend operator fun invoke()
}

class ClearUserSettingsUseCaseImpl @Inject constructor(
    private val repository: UserSettingsRepository
) : ClearUserSettingsUseCase {
    override suspend fun invoke() = repository.removeUserSettings()
}
