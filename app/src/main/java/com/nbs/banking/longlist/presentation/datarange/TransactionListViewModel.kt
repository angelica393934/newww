package com.nbs.banking.longlist.presentation.datarange

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.longlist.domain.usecase.TransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase
) : BaseMviViewModel<TransactionIntent, TransactionEvent, TransactionListState, TransactionEffect>(
    initialState = TransactionListState()
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val items: Flow<PagingData<TransactionUi>> = state
        .map { it.from to it.to }
        .distinctUntilChanged()
        .flatMapLatest { (from, to) -> transactionUseCase(from, to) }
        .map { paging ->
            paging
                .map { TransactionUi.Row(it) }
                .insertSeparators { before, after ->
                    when {
                        after == null -> null
                        before == null || !before.item.date.sameMonth(after.item.date) ->
                            TransactionUi.Header(after.item.date.year, after.item.date.month)

                        else -> null
                    }
                }
        }
        .cachedIn(viewModelScope)


    override fun onIntent(intent: TransactionIntent) {
        when (intent) {
            is TransactionIntent.SetRange -> sendEvent(
                TransactionEvent.RangeChanged(
                    intent.from,
                    intent.to
                )
            )
        }
    }

    override fun reduce(
        currentState: TransactionListState,
        event: TransactionEvent
    ): TransactionListState =
        when (event) {
            is TransactionEvent.RangeChanged -> currentState.copy(from = event.from, to = event.to)
        }
}

private fun LocalDate.sameMonth(other: LocalDate) =
    year == other.year && month.number == other.month.number
