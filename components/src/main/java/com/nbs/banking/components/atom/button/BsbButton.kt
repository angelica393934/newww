package com.nbs.banking.components.atom.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

enum class BsbButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINED,
    TEXT
}

@Composable
fun BsbButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: BsbButtonVariant = BsbButtonVariant.PRIMARY,
    enabled: Boolean = true,
    @DrawableRes leadingIconRes: Int? = null,
    @DrawableRes trailingIconRes: Int? = null,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = Dimension.SIZE_24,
        vertical = Dimension.SIZE_12
    )
) {
    val containerColor: Color
    val contentColor: Color
    val disabledContainerColor: Color
    val disabledContentColor: Color
    var borderStroke: BorderStroke? = null

    when (variant) {
        BsbButtonVariant.PRIMARY -> {
            containerColor = Bsb.Color.baseColor.primary2
            contentColor = Bsb.Color.baseColor.gray50
            disabledContainerColor = Bsb.Color.baseColor.gray200
            disabledContentColor = Bsb.Color.baseColor.gray400
        }
        BsbButtonVariant.SECONDARY -> {
            containerColor = Bsb.Color.baseColor.primary7
            contentColor = Bsb.Color.baseColor.primary1
            disabledContainerColor = Bsb.Color.baseColor.gray200
            disabledContentColor = Bsb.Color.baseColor.gray400
        }
        BsbButtonVariant.OUTLINED -> {
            containerColor = Color.Transparent
            contentColor = Bsb.Color.baseColor.gray950
            disabledContainerColor = Bsb.Color.baseColor.gray200
            disabledContentColor = Bsb.Color.baseColor.gray400
            borderStroke = BorderStroke(Dimension.SIZE_1, Bsb.Color.baseColor.gray100)
        }
        BsbButtonVariant.TEXT -> {
            containerColor = Color.Transparent
            contentColor = Bsb.Color.baseColor.primary1
            disabledContainerColor = Color.Transparent
            disabledContentColor = Bsb.Color.baseColor.gray400
        }
    }

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(Dimension.SIZE_100),
        colors = buttonColors,
        border = if (enabled) borderStroke else null,
        contentPadding = contentPadding
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIconRes != null) {
                Icon(
                    painter = painterResource(id = leadingIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(Dimension.SIZE_24),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(Dimension.SIZE_10))
            }

            Text(
                text = text,
                style = Bsb.Font.button2
            )

            if (trailingIconRes != null) {
                Spacer(modifier = Modifier.width(Dimension.SIZE_10))
                Icon(
                    painter = painterResource(id = trailingIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(Dimension.SIZE_24),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Preview
@Composable
fun BsbButtonPreview() {
    Surface(modifier = Modifier.padding(Dimension.SIZE_16)) {
        Column {
            BsbButton(leadingIconRes = R.drawable.ic_balance, text = "ButtonPrimary", onClick = { }, variant = BsbButtonVariant.PRIMARY)
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            BsbButton(text = "Disabled Primary", onClick = { }, variant = BsbButtonVariant.PRIMARY, enabled = false)
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            BsbButton(text = "ButtonSecondary", onClick = { }, variant = BsbButtonVariant.SECONDARY)
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            BsbButton(text = "ButtonOutlined", onClick = { }, variant = BsbButtonVariant.OUTLINED)
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            BsbButton(text = "Disabled Outlined", onClick = { }, variant = BsbButtonVariant.OUTLINED, enabled = false)
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            BsbButton(text = "ButtonText", onClick = { }, variant = BsbButtonVariant.TEXT)
            Spacer(modifier = Modifier.height(Dimension.SIZE_8))
            BsbButton(text = "Disabled Text", onClick = { }, variant = BsbButtonVariant.TEXT, enabled = false)
        }
    }
}
