package com.nbs.banking.randomactivityidea.navigation

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import com.nbs.banking.randomactivityidea.presentation.RandomActivityIdeaEffect
import com.nbs.banking.randomactivityidea.presentation.BoredScreen
import com.nbs.banking.randomactivityidea.presentation.RandomActivityIdeaViewModel
import com.nbs.banking.navigation.back
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.RandomActivityIdeaDestination

fun EntryProviderScope<AppNavKey>.boredNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<RandomActivityIdeaDestination> {
        val viewModel: RandomActivityIdeaViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val snackbarHostState = remember { SnackbarHostState() }
        val activity = LocalActivity.current as Activity

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is RandomActivityIdeaEffect.ShowSnackBar -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        BoredScreen(
            state = state,
            snackbarHostState = snackbarHostState,
            onBack = { backStack.back({ activity.finish() }) },
            onIntent = viewModel::onIntent
        )
    }
}
