package com.nbs.banking.main

import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.navigation.deeplink.DeepLinkPolicy
import com.nbs.banking.navigation.deeplink.DeepLinkResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainMviViewModel @Inject constructor(
    private val deepLinkResolver: DeepLinkResolver,
    private val deepLinkPolicy: DeepLinkPolicy,
) : BaseMviViewModel<MainIntent, MainEvent, MainState, MainEffect>(MainState()) {

    override fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.DeepLinkReceived -> receiveDeepLink(intent.request)
            MainIntent.DeepLinkConsumed,
            MainIntent.SessionEnded -> sendEvent(MainEvent.DeepLinkCleared)
        }
    }

    override fun reduce(currentState: MainState, event: MainEvent): MainState =
        when (event) {
            is MainEvent.DeepLinkAccepted -> currentState.copy(pendingDeepLink = event.keys)
            MainEvent.DeepLinkCleared -> currentState.copy(pendingDeepLink = null)
        }

    private fun receiveDeepLink(request: DeepLinkRequest) {
        val keys = deepLinkResolver.resolve(request) ?: return
        Timber.tag("App").d("resolved: $keys")
        if (!deepLinkPolicy.isAllowed(keys)) return
        sendEvent(MainEvent.DeepLinkAccepted(keys))
    }
}