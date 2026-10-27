package com.nbs.banking.datastoreexample.navigation

import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import com.nbs.banking.datastoreexample.presentation.UserSettingsEffect
import com.nbs.banking.datastoreexample.presentation.UserSettingsScreen
import com.nbs.banking.datastoreexample.presentation.UserSettingsViewModel
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.UserSettingsDestination

fun EntryProviderScope<AppNavKey>.dataStoreExampleNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<UserSettingsDestination> {
        val viewModel: UserSettingsViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is UserSettingsEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        UserSettingsScreen(
            state = state,
            onIntent = viewModel::onIntent
        )
    }
}
