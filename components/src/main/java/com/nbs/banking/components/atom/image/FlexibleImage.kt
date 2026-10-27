package com.nbs.banking.components.atom.image

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.nbs.banking.components.atom.image.model.ImageConfig
import com.nbs.banking.components.atom.image.model.ImageSize

@Composable
fun FlexibleImage(
    modifier: Modifier = Modifier,
    style: ImageConfig,
) {
    val sizedModifier = modifier.then(
        when (val s = style.size) {
            is ImageSize.Fixed -> Modifier.size(s.width, s.height)
            is ImageSize.FixedWidth -> Modifier.width(s.width)
            is ImageSize.FixedHeight -> Modifier.height(s.height)
            is ImageSize.Ratio -> {
                var m: Modifier = Modifier.aspectRatio(s.aspectRatio)
                s.maxWidth?.let { m = m.widthIn(max = it) }
                m
            }
            is ImageSize.Custom -> s.modifier
        }
    )

    when (style) {
        is ImageConfig.Resource -> {
            Image(
                painter = painterResource(style.id),
                contentDescription = style.contentDescription,
                modifier = sizedModifier,
                contentScale = style.contentScale,
                alignment = style.alignment
            )
        }

        is ImageConfig.Url -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(style.url)
                    .crossfade(style.crossfade)
                    .error(style.error)
                    .placeholder(style.placeholder)
                    .listener(
                        onError = { _, result ->
                            Log.e("FlexibleImage", "Failed to load: ${result.throwable}")
                        }
                    )
                    .build(),
                contentDescription = style.contentDescription,
                modifier = sizedModifier,
                contentScale = style.contentScale,
                alignment = style.alignment
            )
        }
    }
}