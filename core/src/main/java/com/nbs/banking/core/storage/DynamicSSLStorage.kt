package com.nbs.banking.core.storage

import com.nbs.banking.core.datastore.PreferenceDataStore
import com.nbs.banking.core.datastore.SecuredPreferenceDataStore
import com.nbs.banking.core.encryption.encryptedKey
import com.nbs.banking.core.model.DynamicSSL
import javax.inject.Inject

class DynamicSSLStorage @Inject constructor(
    override val preferenceDataStore: PreferenceDataStore,
    override val securePreferenceDataStore: SecuredPreferenceDataStore,
    override val inMemoryStorage: InMemoryStorage,
) : ScopedStorage {
    suspend fun save(data: DynamicSSL) {
        securePreferenceDataStore.save(SECURE_DATA, data)
    }

    fun load(): DynamicSSL? = securePreferenceDataStore.getEager(SECURE_DATA)

    override suspend fun clear() {
        securePreferenceDataStore.remove(SECURE_DATA)
    }
}

private val SECURE_DATA = encryptedKey<DynamicSSL>("DynamicSSL")