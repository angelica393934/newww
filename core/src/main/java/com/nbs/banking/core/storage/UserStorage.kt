package com.nbs.banking.core.storage

import com.nbs.banking.core.datastore.PreferenceDataStore
import com.nbs.banking.core.datastore.SecuredPreferenceDataStore
import javax.inject.Inject

class UserStorage @Inject constructor(
    override val preferenceDataStore: PreferenceDataStore,
    override val securePreferenceDataStore: SecuredPreferenceDataStore,
    override val inMemoryStorage: InMemoryStorage,
) : ScopedStorage {

    override suspend fun clear() {

    }
}
