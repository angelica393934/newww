package com.nbs.banking.corenetwork.session

import kotlinx.coroutines.sync.Mutex

object AnonymousTokenLock {
    private val lock: Mutex = Mutex()

    fun get() = lock
}
