package com.nbs.banking.home.presentation.home.mvi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nbs.banking.R
import com.nbs.banking.core.state.Render
import com.nbs.banking.core.utils.SafeClickWrapper
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun HomeMviScreen(
    state: HomeState,
    snackbarHostState: SnackbarHostState,
    goToDetail: () -> Unit,
    onIntent: (HomeIntent) -> Unit
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Reload Data Button
                SafeClickWrapper(onClick = { onIntent(HomeIntent.LoadHomeContent) }) { action ->
                    Button(onClick = action) {
                        Text(stringResource(R.string.action_reload_data))
                    }
                }

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 3 UI: Direct Flow value (Unread Count)
                Text(
                    text = stringResource(R.string.label_unread_notifications, state.unreadCount),
                    modifier = Modifier.padding(bottom = Dimension.SIZE_16)
                )

                SafeClickWrapper(onClick = goToDetail) { action ->
                    Button(action) {
                        Text(stringResource(R.string.action_detail))
                    }
                }

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 1 UI: Flow + ScreenState (Home Content)
                state.homeContent.Render(
                    onLoading = {
                        Text(stringResource(R.string.message_loading_content))
                    },
                    onSuccess = { content ->
                        Text(content.title)
                        Text(content.description)
                        Text(content.author)
                    },
                    onError = { _, message ->
                        Text(stringResource(R.string.error_message, message))
                    }
                )

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 2 UI: Suspend + ScreenState (Status Check)
                state.status.Render(
                    onLoading = {
                        Text(stringResource(R.string.message_loading_status))
                    },
                    onSuccess = {
                        Text(stringResource(R.string.label_status, it.toString()))
                    },
                    onError = { _, message ->
                        Text(stringResource(R.string.error_message, message))
                    }
                )
                SafeClickWrapper(onClick = { onIntent(HomeIntent.CheckStatus) }) { action ->
                    Button(action) {
                        Text(stringResource(R.string.action_get_status))
                    }
                }

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 4 UI: Suspend + Direct Action (Dismiss Banner)
                SafeClickWrapper(onClick = { onIntent(HomeIntent.DismissBanner("banner_123")) }) { action ->
                    Button(action) {
                        Text(stringResource(R.string.action_dismiss_banner))
                    }
                }
                if (state.bannerActionResult != null) {
                    Spacer(modifier = Modifier.height(Dimension.SIZE_16))
                    Text(state.bannerActionResult)
                }
            }
        }
    }
}