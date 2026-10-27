package com.nbs.banking.longlist.navigation

import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.nbs.banking.core.state.PagingLoadStateEffect
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.longlist.presentation.datarange.TransactionEffect
import com.nbs.banking.longlist.presentation.datarange.TransactionListScreen
import com.nbs.banking.longlist.presentation.datarange.TransactionListViewModel
import com.nbs.banking.longlist.presentation.datarange.TransactionUi
import com.nbs.banking.longlist.presentation.longlist.LongListScreen
import com.nbs.banking.longlist.presentation.longlist.LongListViewModel
import com.nbs.banking.navigation.destination.AppNavKey

fun EntryProviderScope<AppNavKey>.longListNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<LongListDestination> {
        val context = LocalContext.current
        val viewModel: LongListViewModel = hiltViewModel()
        val lazyPagingItems: LazyPagingItems<NumberItem> =
            viewModel.items.collectAsLazyPagingItems()

        PagingLoadStateEffect(
            items = lazyPagingItems,
            onInitialError = {
                Toast.makeText(context, "Failed to load items on initial load", Toast.LENGTH_SHORT)
                    .show()
            },
            onAppendError = {
                Toast.makeText(context, "Failed to load items on load more", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        LongListScreen(lazyPagingState = lazyPagingItems, onIntent = viewModel::onIntent)
    }

    entry<TransactionListDestination> {
        val snackbarHostState = remember { SnackbarHostState() }
        val viewModel: TransactionListViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val lazyPagingItems: LazyPagingItems<TransactionUi> =
           viewModel.items.collectAsLazyPagingItems()

        PagingLoadStateEffect(
            items = lazyPagingItems,
            onInitialError = {

            },
            onAppendError = {

            }
        )

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effectItem ->
                when (effectItem) {
                    is TransactionEffect.ShowMessage -> snackbarHostState.showSnackbar(effectItem.message)
                }
            }
        }

        TransactionListScreen(
            state = state,
            lazyPagingItems = lazyPagingItems,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
        )
    }
}
