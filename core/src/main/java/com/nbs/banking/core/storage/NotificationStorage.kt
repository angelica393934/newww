package com.nbs.banking.core.storage

import com.nbs.banking.core.datastore.PreferenceDataStore
import com.nbs.banking.core.datastore.SecuredPreferenceDataStore
import com.nbs.banking.core.datastore.objectPreferencesKey
import javax.inject.Inject
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class NotificationStorage @Inject constructor(
    override val preferenceDataStore: PreferenceDataStore,
    override val securePreferenceDataStore: SecuredPreferenceDataStore,
    override val inMemoryStorage: InMemoryStorage,
) : ScopedStorage {
    suspend fun setTopics(data: List<String>) {
        preferenceDataStore.saveObject(NOTIFICATION_TOPICS, data)
    }

    fun getTopics(): List<String> {
        return preferenceDataStore.getObjectEager(
            NOTIFICATION_TOPICS,
            emptyList()
        )
    }

    override suspend fun clear() {
        preferenceDataStore.remove(NOTIFICATION_TOPICS.key)
    }
}

private val NOTIFICATION_TOPICS =
    objectPreferencesKey("notification_topics", ListSerializer(String.serializer()))
