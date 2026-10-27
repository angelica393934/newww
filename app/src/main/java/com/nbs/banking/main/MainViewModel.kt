package com.nbs.banking.main

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import com.nbs.banking.navigation.deeplink.DeepLinkPolicy
import com.nbs.banking.navigation.deeplink.DeepLinkResolver
import com.nbs.banking.navigation.destination.AppNavKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject


//TODO: when logged out, consumeDeeplink! (by observer or on logout action)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val deepLinkResolver: DeepLinkResolver,
    private val deepLinkPolicy: DeepLinkPolicy
) : ViewModel() {

    val deeplink: StateFlow<List<AppNavKey>?>
        field = MutableStateFlow<List<AppNavKey>?>(null)

    fun onNewDeepLinkRequest(request: DeepLinkRequest) {
        val keys = deepLinkResolver.resolve(request) ?: return
        Timber.tag("App").d("resolved: $keys")
        if (!deepLinkPolicy.isAllowed(keys)) return
        deeplink.update { keys }
    }

    fun consumeDeeplink() {
        deeplink.update { null }
    }
}
