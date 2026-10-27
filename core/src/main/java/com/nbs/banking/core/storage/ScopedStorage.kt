package com.nbs.banking.core.storage

import com.nbs.banking.core.datastore.PreferenceDataStore
import com.nbs.banking.core.datastore.SecuredPreferenceDataStore

interface ScopedStorage {
    val preferenceDataStore: PreferenceDataStore
    val securePreferenceDataStore: SecuredPreferenceDataStore
    val inMemoryStorage: InMemoryStorage

    suspend fun clear()
}
