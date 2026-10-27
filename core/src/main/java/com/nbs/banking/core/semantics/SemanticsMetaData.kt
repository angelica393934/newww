package com.nbs.banking.core.semantics

import androidx.annotation.Keep

@Keep
data class SemanticsMetaData(
    val testTag: String,
    val contentDescription: String? = null,
)
