package com.nbs.banking.core.utils

import androidx.annotation.ArrayRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import javax.inject.Inject

class StringResourceWrapper @Inject constructor(private val contextProvider: ContextProvider) {
    fun getString(@StringRes resourceId: Int): String = contextProvider.getContext().getString(resourceId)

    fun getStringWithArguments(@StringRes resourceId: Int, vararg arguments: Any): String = contextProvider.getContext().getString(resourceId, *arguments)

    fun getPluralString(
        @PluralsRes resourceId: Int,
        quantity: Int,
        vararg arguments: Any,
    ): String = contextProvider.getContext().resources.getQuantityString(resourceId, quantity, *arguments)

    fun getStringArray(@ArrayRes resourceId: Int): Array<String> = contextProvider.getContext().resources.getStringArray(resourceId)
}
