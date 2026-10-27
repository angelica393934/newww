package com.nbs.banking.components.atom.image.model

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

sealed class ImageSize {
    data class Fixed(val width: Dp, val height: Dp) : ImageSize()
    data class FixedWidth(val width: Dp) : ImageSize()
    data class FixedHeight(val height: Dp) : ImageSize()

    data class Ratio(
        val aspectRatio: Float, // e.g. 16f/9f
        val maxWidth: Dp? = null,
    ) : ImageSize()

    data class Custom(
        val modifier: Modifier,
    ) : ImageSize()
}