package com.nbs.banking.components.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

abstract class BaseBsbColors {
    abstract val baseColor: BsbColor
    abstract val overlay: OverlayColor
    abstract val gradient: GradientColor
}

val LocalBsbColors = staticCompositionLocalOf<BaseBsbColors> {
    BsbColorsLight()
    BsbColorsDark()
}

abstract class BaseBsbTypography {
    abstract val displayHero: TextStyle
    abstract val heading1: TextStyle
    abstract val heading2: TextStyle
    abstract val heading3: TextStyle
    abstract val text1: TextStyle
    abstract val text2: TextStyle
    abstract val text3: TextStyle
    abstract val text4: TextStyle
    abstract val text5: TextStyle
    abstract val text6: TextStyle
    abstract val text7: TextStyle
    abstract val text8: TextStyle
    abstract val caption1: TextStyle
    abstract val caption2: TextStyle
    abstract val caption3: TextStyle
    abstract val button1: TextStyle
    abstract val button2: TextStyle
    abstract val button3: TextStyle
}

val LocalBsbTypography = staticCompositionLocalOf<BaseBsbTypography> {
    BsbTypography()
}
