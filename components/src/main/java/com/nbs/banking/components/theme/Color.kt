package com.nbs.banking.components.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.nbs.banking.components.theme.primitives.Colors

@Immutable
data class BsbColorsDark(
    override val baseColor: BsbColor = BsbColor(),
    override val overlay: OverlayColor = OverlayColor(),
    override val gradient: GradientColor = GradientColor(),
) : BaseBsbColors()


@Immutable
data class BsbColorsLight(
    override val baseColor: BsbColor = BsbColor(),
    override val overlay: OverlayColor = OverlayColor(),
    override val gradient: GradientColor = GradientColor(),
) : BaseBsbColors()


@Immutable
data class BsbColor(
    val primary1: Color = Colors.Primary1,
    val primary2: Color = Colors.Primary2,
    val primary3: Color = Colors.Primary3,
    val primary4: Color = Colors.Primary4,
    val primary5: Color = Colors.Primary5,
    val primary6: Color = Colors.Primary6,
    val primary7: Color = Colors.Primary7,
    val primary8: Color = Colors.Primary8,

    val gray950: Color = Colors.Gray950,
    val gray900: Color = Colors.Gray900,
    val gray800: Color = Colors.Gray800,
    val gray700: Color = Colors.Gray700,
    val gray600: Color = Colors.Gray600,
    val gray500: Color = Colors.Gray500,
    val gray400: Color = Colors.Gray400,
    val gray300: Color = Colors.Gray300,
    val gray200: Color = Colors.Gray200,
    val gray100: Color = Colors.Gray100,
    val gray50: Color = Colors.Gray50,

    val green950: Color = Colors.Green950,
    val green900: Color = Colors.Green900,
    val green800: Color = Colors.Green800,
    val green700: Color = Colors.Green700,
    val green600: Color = Colors.Green600,
    val green500: Color = Colors.Green500,
    val green400: Color = Colors.Green400,
    val green300: Color = Colors.Green300,
    val green200: Color = Colors.Green200,
    val green100: Color = Colors.Green100,
    val green50: Color = Colors.Green50,

    val orange950: Color = Colors.Orange950,
    val orange900: Color = Colors.Orange900,
    val orange800: Color = Colors.Orange800,
    val orange700: Color = Colors.Orange700,
    val orange600: Color = Colors.Orange600,
    val orange500: Color = Colors.Orange500,
    val orange400: Color = Colors.Orange400,
    val orange300: Color = Colors.Orange300,
    val orange200: Color = Colors.Orange200,
    val orange100: Color = Colors.Orange100,
    val orange50: Color = Colors.Orange50,

    val red950: Color = Colors.Red950,
    val red900: Color = Colors.Red900,
    val red800: Color = Colors.Red800,
    val red700: Color = Colors.Red700,
    val red600: Color = Colors.Red600,
    val red500: Color = Colors.Red500,
    val red400: Color = Colors.Red400,
    val red300: Color = Colors.Red300,
    val red200: Color = Colors.Red200,
    val red100: Color = Colors.Red100,
    val red50: Color = Colors.Red50,
)

@Immutable
data class OverlayColor(
    val primary1Alpha70: Color = Color(0xFF181C21).copy(alpha = 0.7f),
)

@Immutable
data class GradientColor(
    val regular: Brush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF0386CD), Color(0xFFFFFFFF))
    ),
)
