package com.nbs.banking.navigation.shortcut

import com.nbs.banking.navigation.destination.NavigationDestination

enum class ShortcutFeatureType {
    UNKNOWN,
    QUICK_ACCESS,
    ;

    companion object {
        fun getNameByDestination(destination: NavigationDestination) = when (destination) {

            else -> UNKNOWN.name
        }
    }
}
