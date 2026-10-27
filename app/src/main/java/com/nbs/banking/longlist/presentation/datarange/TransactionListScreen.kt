package com.nbs.banking.longlist.presentation.datarange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.nbs.banking.R
import com.nbs.banking.core.state.Render
import com.nbs.banking.core.state.RenderAppend
import com.nbs.banking.longlist.domain.model.Transaction
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private fun LocalDate.toHumanReadable(): String {
    val formatter =
        java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.getDefault())
    return toJavaLocalDate().format(formatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    state: TransactionListState,
    lazyPagingItems: LazyPagingItems<TransactionUi>,
    snackbarHostState: SnackbarHostState,
    onIntent: (TransactionIntent) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "${state.from.toHumanReadable()} → ${state.to.toHumanReadable()}",
                        style = Bsb.Font.heading3,
                        color = Bsb.Color.baseColor.primary1
                    ) 
                },
                actions = {
                    TextButton(onClick = { showPicker = true }) {
                        Text(stringResource(R.string.action_range))
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            lazyPagingItems.Render(
                onLoading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                onError = { _, message ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.error_message, message),
                                style = Bsb.Font.text1,
                                modifier = Modifier.padding(Dimension.SIZE_16)
                            )
                            Button(onClick = { lazyPagingItems.refresh() }) { Text(stringResource(R.string.action_retry)) }
                        }
                    }
                },

                onEmpty = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.message_no_transactions),
                            style = Bsb.Font.text1
                        )
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimension.SIZE_16, vertical = Dimension.SIZE_8),
                    verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_8)
                ) {
                    items(
                        count = lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { it.key },
                        contentType = lazyPagingItems.itemContentType { it::class },
                    ) { index ->
                        val item = lazyPagingItems[index]
                        when (val ui = item) {
                            is TransactionUi.Header -> MonthHeader(ui.year, ui.month)
                            is TransactionUi.Row -> TransactionRow(ui.item)
                            null -> Unit
                        }
                    }

                    RenderAppend(
                        items = lazyPagingItems,
                        onLoading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimension.SIZE_16),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        onError = { _, message ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Dimension.SIZE_16),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.error_failed_to_load_more, message),
                                        style = Bsb.Font.text2
                                    )
                                    Button(
                                        onClick = { lazyPagingItems.retry() },
                                        modifier = Modifier.padding(top = Dimension.SIZE_8)
                                    ) {
                                        Text(stringResource(R.string.action_retry))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    if (showPicker) {
        RangePickerDialog(
            from = state.from,
            to = state.to,
            onDismiss = { showPicker = false },
            onApply = { fromDate, toDate ->
                onIntent(TransactionIntent.SetRange(fromDate, toDate))
                showPicker = false
            },
        )
    }
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimension.SIZE_16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.title, style = Bsb.Font.heading3)
                Text(
                    text = transaction.date.toHumanReadable(),
                    style = Bsb.Font.text3
                )
            }
            Text(
                text = "$${transaction.amountCents / 100.0}",
                style = Bsb.Font.heading3
            )
        }
    }
}

@Composable
private fun RangePickerDialog(
    from: LocalDate,
    to: LocalDate,
    onDismiss: () -> Unit,
    onApply: (LocalDate, LocalDate) -> Unit,
) {
    val picker = rememberDateRangePickerState(
        initialSelectedStartDateMillis = from.atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds(),
        initialSelectedEndDateMillis = to.atStartOfDayIn(TimeZone.UTC)
            .toEpochMilliseconds(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = picker.selectedStartDateMillis != null && picker.selectedEndDateMillis != null,
                onClick = {
                    fun Long.toDate() =
                        Instant.fromEpochMilliseconds(this)
                            .toLocalDateTime(TimeZone.UTC).date
                    onApply(
                        picker.selectedStartDateMillis!!.toDate(),
                        picker.selectedEndDateMillis!!.toDate(),
                    )
                },
            ) { Text(stringResource(R.string.action_apply)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    ) {
        DateRangePicker(state = picker, modifier = Modifier.height(500.dp))
    }
}


@Composable
private fun MonthHeader(year: Int, month: Month) {
    val title = "${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year"
    Text(
        text = title,
        color = Bsb.Color.baseColor.gray50,
        style = Bsb.Font.heading2,
        modifier = Modifier
            .fillMaxWidth()
            .background(Bsb.Color.baseColor.gray800)
            .padding(horizontal = Dimension.SIZE_16, vertical = Dimension.SIZE_8),
    )
}