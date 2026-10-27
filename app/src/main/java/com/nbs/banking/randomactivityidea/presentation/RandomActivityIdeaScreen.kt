package com.nbs.banking.randomactivityidea.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.nbs.banking.core.state.isLoading
import com.nbs.banking.core.utils.SafeClickWrapper
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun BoredScreen(
    state: RandomActivityIdeaState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (RandomActivityIdeaIntent) -> Unit
) {

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimension.SIZE_16),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.title_random_activity),
                    style = Bsb.Font.heading2,
                    color = Bsb.Color.baseColor.primary1
                )

                Spacer(modifier = Modifier.height(Dimension.SIZE_24))

                Button(
                    onClick = dropUnlessResumed { onIntent(RandomActivityIdeaIntent.FetchRandomActivity) },
                    enabled = !state.activityState.isLoading(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (state.activityState.isLoading()) stringResource(R.string.action_fetching) else stringResource(R.string.action_get_random_activity))
                }


                Spacer(modifier = Modifier.height(Dimension.SIZE_24))

                state.activityState.Render(
                    onLoading = { Text(stringResource(R.string.message_finding_activity), style = Bsb.Font.text1) },
                    onSuccess = { activity ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimension.SIZE_16)
                            ) {
                                Text(
                                    text = activity.activity,
                                    style = Bsb.Font.heading3
                                )
                                Spacer(modifier = Modifier.height(Dimension.SIZE_8))
                                Text(text = stringResource(R.string.label_type, activity.type.replaceFirstChar { it.uppercase() }), style = Bsb.Font.text2)
                                Text(text = stringResource(R.string.label_participants, activity.participants.toString()), style = Bsb.Font.text2)
                                Text(text = stringResource(R.string.label_price, activity.price.toString()), style = Bsb.Font.text2)
                                if (activity.link.isNotEmpty()) {
                                    Text(text = stringResource(R.string.label_link, activity.link), style = Bsb.Font.text2)
                                }
                            }
                        }
                    },
                    onError = { _, message ->
                        Text(stringResource(R.string.error_message, message), style = Bsb.Font.text1)
                    },
                    onDefault = {
                        Text(stringResource(R.string.message_tap_button_random_activity), style = Bsb.Font.text1)
                    }
                )

                Spacer(modifier = Modifier.height(Dimension.SIZE_16))

                SafeClickWrapper({ onBack() }) { action ->
                    Button(
                        onClick = action,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.action_back))
                    }
                }
            }
        }
    }
}
