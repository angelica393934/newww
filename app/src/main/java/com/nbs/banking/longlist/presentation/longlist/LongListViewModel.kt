package com.nbs.banking.longlist.presentation.longlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.longlist.domain.usecase.LongListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LongListViewModel @Inject constructor(
    longListUseCase: LongListUseCase
) : BaseMviViewModel<LongListIntent, LongListEvent, LongListState, LongListEffect>(LongListState) {

    val items: Flow<PagingData<NumberItem>> = longListUseCase().cachedIn(viewModelScope)

    override fun onIntent(intent: LongListIntent) = Unit

    override fun reduce(
        currentState: LongListState,
        event: LongListEvent
    ): LongListState = currentState
}
