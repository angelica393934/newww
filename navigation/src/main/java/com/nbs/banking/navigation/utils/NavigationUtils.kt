package com.nbs.banking.navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer
import com.nbs.banking.navigation.destination.AppNavKey

@Composable
fun rememberAppNavBackStack(vararg elements: AppNavKey): NavBackStack<AppNavKey> {
    return rememberSerializable(
        serializer = NavBackStackSerializer(elementSerializer = NavKeySerializer())
    ) {
        NavBackStack(*elements)
    }
}