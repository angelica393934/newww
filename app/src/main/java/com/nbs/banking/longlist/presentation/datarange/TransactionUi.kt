package com.nbs.banking.longlist.presentation.datarange

import com.nbs.banking.longlist.domain.model.Transaction
import kotlinx.datetime.Month

sealed interface TransactionUi {
    val key: String

    data class Header(val year: Int, val month: Month) : TransactionUi {
        override val key = "h_${year}_${month.ordinal}"
    }

    data class Row(val item: Transaction) : TransactionUi {
        override val key = "t_${item.id}"
    }
}