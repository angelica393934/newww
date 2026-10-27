package com.nbs.banking.components.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.toFontFamily
import com.nbs.banking.components.theme.primitives.Fonts
import com.nbs.banking.components.theme.primitives.TextSize

@Immutable
data class BsbTypography(
    override val displayHero: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_36,
        lineHeight = TextSize.SIZE_38,
        letterSpacing = TextSize.SIZE_0
    ),
    override val heading1: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_24,
        lineHeight = TextSize.SIZE_34,
        letterSpacing = TextSize.SIZE_0
    ),
    override val heading2: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_20,
        lineHeight = TextSize.SIZE_30,
        letterSpacing = TextSize.SIZE_0
    ),
    override val heading3: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansExtraBold.toFontFamily(),
        fontSize = TextSize.SIZE_16,
        lineHeight = TextSize.SIZE_26,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text1: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansRegular.toFontFamily(),
        fontSize = TextSize.SIZE_16,
        lineHeight = TextSize.SIZE_24,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text2: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansMedium.toFontFamily(),
        fontSize = TextSize.SIZE_16,
        lineHeight = TextSize.SIZE_24,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text3: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_16,
        lineHeight = TextSize.SIZE_24,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text4: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansRegular.toFontFamily(),
        fontSize = TextSize.SIZE_14,
        lineHeight = TextSize.SIZE_20,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text5: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansMedium.toFontFamily(),
        fontSize = TextSize.SIZE_14,
        lineHeight = TextSize.SIZE_20,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text6: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_14,
        lineHeight = TextSize.SIZE_20,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text7: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansMedium.toFontFamily(),
        fontSize = TextSize.SIZE_10,
        lineHeight = TextSize.SIZE_14,
        letterSpacing = TextSize.SIZE_0
    ),
    override val text8: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_10,
        lineHeight = TextSize.SIZE_14,
        letterSpacing = TextSize.SIZE_0
    ),
    override val caption1: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansRegular.toFontFamily(),
        fontSize = TextSize.SIZE_12,
        lineHeight = TextSize.SIZE_16,
        letterSpacing = TextSize.SIZE_0
    ),
    override val caption2: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansMedium.toFontFamily(),
        fontSize = TextSize.SIZE_12,
        lineHeight = TextSize.SIZE_16,
        letterSpacing = TextSize.SIZE_0
    ),
    override val caption3: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_12,
        lineHeight = TextSize.SIZE_16,
        letterSpacing = TextSize.SIZE_0
    ),
    override val button1: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_12,
        lineHeight = TextSize.SIZE_16,
        letterSpacing = TextSize.SIZE_0
    ),
    override val button2: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_14,
        lineHeight = TextSize.SIZE_20,
        letterSpacing = TextSize.SIZE_0
    ),
    override val button3: TextStyle = TextStyle(
        fontFamily = Fonts.plusJakartaSansBold.toFontFamily(),
        fontSize = TextSize.SIZE_12,
        lineHeight = TextSize.SIZE_16,
        letterSpacing = TextSize.SIZE_0
    ),
) : BaseBsbTypography()

