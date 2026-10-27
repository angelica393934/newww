package com.nbs.banking.components.molecule.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

enum class BsbTopBarVariant {
    STANDARD,
    WITH_HANDLE,
    HANDLE_ONLY,
    CIRCLE_BUTTONS,
    LARGE_TITLE,
    BRANDED
}

@Composable
fun BsbTopBar(
    title: String = "Placeholder",
    modifier: Modifier = Modifier,
    variant: BsbTopBarVariant = BsbTopBarVariant.STANDARD,
    onBackClick: (() -> Unit)? = null,
    onCloseClick: (() -> Unit)? = null
) {
    val colors = Bsb.Color.baseColor
    val isBranded = variant == BsbTopBarVariant.BRANDED
    val isCircular = variant == BsbTopBarVariant.CIRCLE_BUTTONS
    val isHandle = variant == BsbTopBarVariant.WITH_HANDLE || variant == BsbTopBarVariant.HANDLE_ONLY
    val showActions = variant != BsbTopBarVariant.HANDLE_ONLY
    val iconColor = if (isBranded || isCircular) Color.White else colors.gray950
    val height = when (variant) {
        BsbTopBarVariant.STANDARD -> Dimension.SIZE_72
        BsbTopBarVariant.WITH_HANDLE -> Dimension.SIZE_72
        BsbTopBarVariant.HANDLE_ONLY -> Dimension.SIZE_48
        BsbTopBarVariant.CIRCLE_BUTTONS -> Dimension.SIZE_100
        BsbTopBarVariant.LARGE_TITLE -> Dimension.SIZE_80
        BsbTopBarVariant.BRANDED -> Dimension.SIZE_100
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(
                if (isBranded) {
                    Modifier.clip(
                        RoundedCornerShape(
                            bottomStart = Dimension.SIZE_32,
                            bottomEnd = Dimension.SIZE_32
                        )
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isBranded) {
            Image(
                painter = painterResource(id = R.drawable.bg_top_bar),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = Dimension.SIZE_30,
                            bottomEnd = Dimension.SIZE_30
                        )
                    ),
                contentScale = ContentScale.FillWidth
            )
        }

        if (isHandle) {
            Icon(
                painter = painterResource(id = R.drawable.ic_line),
                contentDescription = null,
                modifier = Modifier.size(
                    width = if (variant == BsbTopBarVariant.HANDLE_ONLY) Dimension.SIZE_80 else Dimension.SIZE_60,
                    height = Dimension.SIZE_8
                ),
                tint = colors.gray400
            )
        } else {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimension.SIZE_80),
                style = if (variant == BsbTopBarVariant.STANDARD) Bsb.Font.heading3 else Bsb.Font.heading2,
                color = if (isBranded) Color.White else colors.gray950,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        if (showActions) {
            BsbTopBarAction(
                iconRes = R.drawable.ic_chevron_left,
                contentDescription = stringResource(R.string.action_back),
                onClick = onBackClick,
                circular = isCircular,
                tint = iconColor,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = Dimension.SIZE_24)
            )

            BsbTopBarAction(
                iconRes = R.drawable.ic_close,
                contentDescription = stringResource(R.string.action_close),
                onClick = onCloseClick,
                circular = isCircular,
                tint = iconColor,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = Dimension.SIZE_24)
            )
        }
    }
}

@Composable
private fun BsbTopBarAction(
    iconRes: Int,
    contentDescription: String,
    onClick: (() -> Unit)?,
    circular: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val colors = Bsb.Color.baseColor

    Box(
        modifier = modifier
            .size(if (circular) Dimension.SIZE_50 else Dimension.SIZE_40)
            .then(
                if (circular) {
                    Modifier
                        .clip(CircleShape)
                        .background(colors.primary2)
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(Dimension.SIZE_24),
            tint = tint
        )
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 720)
@Composable
private fun BsbTopBarPreview() {
    Surface {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_24)
        ) {
            BsbTopBar(
                title = "Placeholder",
                variant = BsbTopBarVariant.STANDARD,
                onBackClick = {},
                onCloseClick = {}
            )

            BsbTopBar(
                variant = BsbTopBarVariant.WITH_HANDLE,
                onBackClick = {},
                onCloseClick = {}
            )

            BsbTopBar(
                variant = BsbTopBarVariant.HANDLE_ONLY
            )

            BsbTopBar(
                title = "Placeholder",
                variant = BsbTopBarVariant.CIRCLE_BUTTONS,
                onBackClick = {},
                onCloseClick = {}
            )

            BsbTopBar(
                title = "Placeholder",
                variant = BsbTopBarVariant.LARGE_TITLE,
                onBackClick = {},
                onCloseClick = {}
            )

            BsbTopBar(
                title = "Placeholder",
                variant = BsbTopBarVariant.BRANDED,
                onBackClick = {},
                onCloseClick = {}
            )
        }
    }
}