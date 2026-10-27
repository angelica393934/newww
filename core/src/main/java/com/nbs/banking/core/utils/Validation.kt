package com.nbs.banking.core.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A safe version of the check API.
 * Unlike the standard check API, this version does not throw an IllegalStateException;
 * instead, it invokes the onFail function.
 */
@OptIn(ExperimentalContracts::class)
inline fun safeCheck(condition: Boolean, onFail: () -> Unit = {}) {
    contract {
        returns() implies condition
        callsInPlace(onFail, InvocationKind.AT_MOST_ONCE)
    }

    if (!condition) onFail()
}
