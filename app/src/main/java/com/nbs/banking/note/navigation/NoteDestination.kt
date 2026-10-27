package com.nbs.banking.note.navigation

import com.nbs.banking.navigation.destination.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data object AddNoteDestination : NavigationDestination {
    const val RESULT_KEY = "AddNoteDestinationResultKey"
}