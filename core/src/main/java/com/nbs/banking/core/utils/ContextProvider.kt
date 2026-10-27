package com.nbs.banking.core.utils

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextProvider @Inject constructor(context: Context) {
    private var currentContext: Context = context

    fun updateContext(newContext: Context) {
        currentContext = newContext
    }

    fun getContext(): Context = currentContext
}
