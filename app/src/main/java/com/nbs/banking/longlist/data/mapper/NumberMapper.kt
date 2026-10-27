package com.nbs.banking.longlist.data.mapper

import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import com.nbs.banking.longlist.domain.model.NumberItem
import com.nbs.banking.longlist.domain.model.Transaction


fun NumberResponse.toDomain() = NumberItem(
    number = number,
)

fun TransactionResponse.toDomain() = Transaction(
    id = id, date = date, title = title, amountCents = amountCents
)