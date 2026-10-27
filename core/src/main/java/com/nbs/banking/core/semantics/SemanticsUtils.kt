package com.nbs.banking.core.semantics

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.addSemantics(semanticsMetaData: SemanticsMetaData): Modifier = this.semantics {
    this.testTagsAsResourceId = true
    this.testTag = semanticsMetaData.testTag
    semanticsMetaData.contentDescription
        ?.takeIf { it.isNotBlank() }
        ?.let { contentDescription ->
            this.contentDescription = contentDescription
        }
}
