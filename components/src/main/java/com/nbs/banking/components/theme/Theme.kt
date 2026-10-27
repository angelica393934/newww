package com.nbs.banking.components.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun BsbTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val bsbColors = if (darkTheme) BsbColorsDark() else BsbColorsLight()
    val bsbTypography = BsbTypography()

    CompositionLocalProvider(
        LocalBsbColors provides bsbColors,
        LocalBsbTypography provides bsbTypography,
    ) {
        MaterialTheme {
            CompositionLocalProvider(
                content = content
            )
        }
    }
}

object Bsb {
    val Color: BaseBsbColors
        @Composable
        get() = LocalBsbColors.current

    val Font: BaseBsbTypography
        @Composable
        get() = LocalBsbTypography.current
}