package com.nbs.banking.components.atom.image.model

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

sealed class ImageConfig {
    open val contentDescription: String? = null
    open val contentScale: ContentScale = ContentScale.Crop
    open val alignment: Alignment = Alignment.Center
    open val size: ImageSize = ImageSize.Custom(Modifier.fillMaxWidth())

    data class Resource(
        @field:DrawableRes val id: Int,
        override val contentDescription: String? = null,
        override val contentScale: ContentScale = ContentScale.Crop,
        override val alignment: Alignment = Alignment.Center,
        override val size: ImageSize = ImageSize.Custom(Modifier.fillMaxWidth())
    ) : ImageConfig()

    data class Url(
        val url: String,
        @field:DrawableRes val placeholder: Int = 0,
        @field:DrawableRes val error: Int = placeholder,
        val crossfade: Boolean = true,
        override val contentDescription: String? = null,
        override val contentScale: ContentScale = ContentScale.Crop,
        override val alignment: Alignment = Alignment.Center,
        override val size: ImageSize = ImageSize.Custom(Modifier.fillMaxWidth())
    ) : ImageConfig()
}
