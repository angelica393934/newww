package com.nbs.banking.navigation.utils

import androidx.navigation3.runtime.NavEntryDecorator
import timber.log.Timber

class CustomNavEntryDecorator<T : Any> : NavEntryDecorator<T>(
    onPop = { contentKey ->
        Timber.tag("CustomNavEntryDecorator").d("entry with $contentKey was popped")
    },
    decorate = { entry ->
        Timber.tag("CustomNavEntryDecorator")
            .d("entry with ${entry.contentKey} entered composition and was decorated")
        entry.Content()
    }
)