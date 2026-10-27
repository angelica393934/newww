package com.nbs.banking.home.presentation.home.mvvm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.compose.dropUnlessResumed
import com.nbs.banking.R
import com.nbs.banking.core.state.Render
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.core.utils.SafeClickWrapper
import com.nbs.banking.home.domain.dummyhome.model.HomeContent
import com.nbs.banking.components.theme.primitives.Dimension

data class HomeScreenUiState(
    val result: String? = null,
    val status: ScreenState<Boolean> = ScreenState.default(),
    val homeContent: ScreenState<HomeContent> = ScreenState.default(),
    val unreadCount: Int = 0,
    val bannerActionResult: String? = null,
)

@Composable
fun HomeScreen(
    uiState: HomeScreenUiState,
    snackbarHostState: SnackbarHostState,
    checkStatus: () -> Unit,
    onReloadData: () -> Unit,
    onDismissBanner: (String) -> Unit,
    onDetailClicked: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                SafeClickWrapper(onClick = onReloadData) { action ->
                    Button(onClick = action) {
                        Text(stringResource(R.string.action_reload_data))
                    }
                }

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 3 UI: Direct Flow value (Unread Count)
                Text(
                    text = stringResource(R.string.label_unread_notifications, uiState.unreadCount),
                    modifier = Modifier.padding(bottom = Dimension.SIZE_16)
                )

                Button(
                    dropUnlessResumed { onDetailClicked() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_detail))
                }

                if (uiState.result != null) {
                    Spacer(modifier = Modifier.height(Dimension.SIZE_8))
                    Text("result = ${uiState.result}")
                }

                // Case 1 UI: Flow + ScreenState (Home Content)
                uiState.homeContent.Render(
                    onLoading = { Text(stringResource(R.string.message_loading_content)) },
                    onSuccess = { data ->
                        Text(data.title)
                        Text(data.description)
                        Text(data.author)
                    },
                    onError = { _, message ->
                        Text(stringResource(R.string.error_message, message))
                    },
                    onDefault = { }
                )

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 2 UI: Suspend + ScreenState (Status)
                uiState.status.Render(
                    onLoading = { Text(stringResource(R.string.message_loading_status)) },
                    onSuccess = { data ->
                        Text(stringResource(R.string.label_status, data.toString()))
                    },
                    onError = { _, message ->
                        Text(stringResource(R.string.error_message, message))
                    },
                    onDefault = { }
                )
                SafeClickWrapper(onClick = checkStatus) { action ->
                    Button(action) {
                        Text(stringResource(R.string.action_get_status))
                    }
                }

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                // Case 4 UI: Suspend + Direct Action (Dismiss Banner)
                SafeClickWrapper(onClick = { onDismissBanner("banner_123") }) { action ->
                    Button(action) {
                        Text(stringResource(R.string.action_dismiss_banner))
                    }
                }
                if (uiState.bannerActionResult != null) {
                    Spacer(modifier = Modifier.height(Dimension.SIZE_8))
                    Text(uiState.bannerActionResult)
                }
            }
        }
    }
}

@Composable
fun DetailScreen(id: String, onBack: () -> Unit) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("id: $id")
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            SafeClickWrapper(onClick = onBack) { action ->
                Button(action) {
                    Text(stringResource(R.string.action_back))
                }
            }
        }
    }
}