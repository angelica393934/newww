package com.nbs.banking.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.invoke
import com.nbs.banking.App
import com.nbs.banking.core.utils.isFromHistory
import com.nbs.banking.components.theme.BsbTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainMviViewModel by viewModels()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Timber.tag("app").d("onNewIntent: $intent")
        if (!intent.isFromHistory()) {
            viewModel.onIntent(MainIntent.DeepLinkReceived(DeepLinkRequest(intent)))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.tag("app").d("onCreate: $intent")
        if (savedInstanceState == null && !intent.isFromHistory()) {
            viewModel.onIntent(MainIntent.DeepLinkReceived(DeepLinkRequest(intent)))
        }

        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            BsbTheme {
                App(
                    deeplinkDestination = state.pendingDeepLink,
                    onDeepLinkConsumed = { viewModel.onIntent(MainIntent.DeepLinkConsumed) }
                )
            }
        }
    }
}