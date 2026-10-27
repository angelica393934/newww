package com.nbs.banking.datastoreexample.domain.usecase

import com.nbs.banking.datastoreexample.domain.model.UserSettings
import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import javax.inject.Inject

interface GetUserSettingsEagerUseCase {
    operator fun invoke(): UserSettings
}

class GetUserSettingsEagerUseCaseImpl @Inject constructor(
    private val repository: UserSettingsRepository
) : GetUserSettingsEagerUseCase {
    override operator fun invoke(): UserSettings = repository.getUserSettingsEager()
}
