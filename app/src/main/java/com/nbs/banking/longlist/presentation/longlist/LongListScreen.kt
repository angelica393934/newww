package com.nbs.banking.longlist.presentation.longlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.nbs.banking.R
import com.nbs.banking.core.state.Render
import com.nbs.banking.core.state.RenderAppend
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongListScreen(
    lazyPagingState: LazyPagingItems<NumberItem>,
    onIntent: (LongListIntent) -> Unit
) {

    val isRefreshing = lazyPagingState.loadState.refresh is LoadState.Loading &&
            lazyPagingState.itemCount > 0

    Scaffold(
        topBar = {
            TopAppBar(title = { 
                Text(
                    text = stringResource(R.string.title_long_list),
                    style = Bsb.Font.heading3,
                    color = Bsb.Color.baseColor.primary1
                ) 
            })
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = lazyPagingState::refresh,
            modifier = Modifier.padding(paddingValues),
        ) {
            lazyPagingState.Render(
                onLoading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                },
                onError = { _, message ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = stringResource(R.string.error_message, message), style = Bsb.Font.text1)
                        Button(onClick = lazyPagingState::refresh) { Text(stringResource(R.string.action_retry)) }
                    }
                },
                onEmpty = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) { Text(stringResource(R.string.message_no_items_found), style = Bsb.Font.text1) }
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        count = lazyPagingState.itemCount,
                        key = lazyPagingState.itemKey { it.number },
                    ) { index ->
                        lazyPagingState[index]?.let { item ->
                            Text(
                                text = stringResource(R.string.label_item_number, item.number),
                                style = Bsb.Font.text1,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimension.SIZE_16),
                            )
                        }
                    }
                    RenderAppend(
                        items = lazyPagingState,
                        onLoading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimension.SIZE_16),
                                contentAlignment = Alignment.Center,
                            ) { CircularProgressIndicator() }
                        },
                        onError = { _, message ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimension.SIZE_16),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                            ) {
                                Text(text = stringResource(R.string.error_message, message), style = Bsb.Font.text2)

                                Button(onClick = lazyPagingState::retry) {
                                    Text(stringResource(R.string.action_retry))
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
