package com.nbs.banking.corenetwork.session

import kotlinx.coroutines.sync.Mutex

object RefreshSessionLock {
    private val lock: Mutex = Mutex()

    @Volatile
    private var isRefreshing: Boolean = false

    fun get() = lock

    fun isRefreshing() = isRefreshing

    fun markRefreshing() {
        isRefreshing = true
    }

    fun markRefreshFinished() {
        isRefreshing = false
    }
}
