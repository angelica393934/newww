package com.nbs.banking.core.callguard

data class PrivacyLatchState(
    val finalCallState: CallState = CallState.IDLE,
    val wasVoipActiveBefore: Boolean = false,
    val isWaitingForHardwareToClear: Boolean = false,
)
