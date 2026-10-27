package com.nbs.banking.navigation.utils

import android.util.LruCache
import com.nbs.banking.core.utils.NavigationKeyEnum
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface NavigationDataManager {
    fun <T : Any> put(key: String, value: T)

    fun <T : Any> get(key: String): T?

    fun remove(key: String)

    fun generateKey(tag: NavigationKeyEnum): String
}

@Singleton
class NavigationDataManagerImpl @Inject constructor() : NavigationDataManager {
    companion object {
        private const val MAX_ITEMS = 5
    }

    private val store = object : LruCache<String, Any>(MAX_ITEMS) {}

    override fun <T : Any> put(key: String, value: T) {
        store.put(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(key: String): T? = store.get(key) as? T

    override fun remove(key: String) {
        store.remove(key)
    }

    override fun generateKey(tag: NavigationKeyEnum): String = "${tag.name}-${UUID.randomUUID()}"
}
