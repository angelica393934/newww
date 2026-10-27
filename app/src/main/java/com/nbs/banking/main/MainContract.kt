package com.nbs.banking.main

import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import com.nbs.banking.navigation.destination.AppNavKey

data class MainState(
    val pendingDeepLink: List<AppNavKey>? = null,
)

sealed interface MainIntent {
    data class DeepLinkReceived(val request: DeepLinkRequest) : MainIntent
    data object DeepLinkConsumed : MainIntent
    data object SessionEnded : MainIntent
}

sealed interface MainEvent {
    data class DeepLinkAccepted(val keys: List<AppNavKey>) : MainEvent
    data object DeepLinkCleared : MainEvent
}

sealed interface MainEffect