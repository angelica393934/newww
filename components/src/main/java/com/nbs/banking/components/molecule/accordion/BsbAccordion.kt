package com.nbs.banking.components.molecule.accordion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.BsbTheme
import com.nbs.banking.components.theme.primitives.Dimension

enum class BsbAccordionVariant {
    Outlined,
    Plain
}

@Composable
fun BsbAccordion(
    title: String,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false,
    variant: BsbAccordionVariant = BsbAccordionVariant.Outlined,
    content: @Composable () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(initialExpanded) }

    BsbAccordion(
        title = title,
        isExpanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
        variant = variant,
        content = content
    )
}

@Composable
fun BsbAccordion(
    title: String,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    variant: BsbAccordionVariant = BsbAccordionVariant.Outlined,
    content: @Composable () -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "Chevron Rotation"
    )

    val surfaceModifier = modifier.fillMaxWidth()
    val shape = RoundedCornerShape(Dimension.SIZE_10)
    val border = if (variant == BsbAccordionVariant.Outlined) {
        BorderStroke(Dimension.SIZE_1, Bsb.Color.baseColor.gray200)
    } else {
        null
    }
    val surfaceColor = if (variant == BsbAccordionVariant.Outlined) {
        Color.White
    } else {
        Color.Transparent
    }

    Surface(
        modifier = surfaceModifier,
        shape = shape,
        border = border,
        color = surfaceColor
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!isExpanded) }
                    .padding(Dimension.SIZE_16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = Bsb.Font.text6,
                    color = Bsb.Color.baseColor.gray950,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down),
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = Bsb.Color.baseColor.gray950,
                    modifier = Modifier.rotate(rotationState)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (variant == BsbAccordionVariant.Outlined) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Dimension.SIZE_16),
                            color = Bsb.Color.baseColor.gray200,
                            thickness = Dimension.SIZE_1
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = Dimension.SIZE_16,
                                end = Dimension.SIZE_16,
                                bottom = Dimension.SIZE_16,
                                top = if (variant == BsbAccordionVariant.Outlined) Dimension.SIZE_16 else Dimension.SIZE_0
                            )
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BsbAccordionOutlinedPreview() {
    BsbTheme {
        Column(Modifier.padding(Dimension.SIZE_16)) {
            BsbAccordion(
                title = "Apa Itu Mobile Banking BSB?",
                initialExpanded = false,
                variant = BsbAccordionVariant.Outlined
            ) {
                Text(
                    text = "Bank SumselBabel Mobile Merupakan Layanan Mobile Banking Dari PT Bank Pembangunan Daerah Sumatera Selatan Dan Bangka Belitung...",
                    style = Bsb.Font.text4,
                    color = Bsb.Color.baseColor.gray600
                )
            }
            Spacer(Modifier.padding(Dimension.SIZE_8))
            BsbAccordion(
                title = "Apa Itu Mobile Banking BSB?",
                initialExpanded = true,
                variant = BsbAccordionVariant.Outlined
            ) {
                Text(
                    text = "Bank SumselBabel Mobile Merupakan Layanan Mobile Banking Dari PT Bank Pembangunan Daerah Sumatera Selatan Dan Bangka Belitung...",
                    style = Bsb.Font.text4,
                    color = Bsb.Color.baseColor.gray600
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BsbAccordionPlainPreview() {
    BsbTheme {
        Column(Modifier.padding(Dimension.SIZE_16)) {
            BsbAccordion(
                title = "Pajak dan Pendidikan",
                initialExpanded = false,
                variant = BsbAccordionVariant.Plain
            ) {
                Text("Content inside plain accordion")
            }
            Spacer(Modifier.padding(Dimension.SIZE_8))
            BsbAccordion(
                title = "Pajak dan Pendidikan",
                initialExpanded = true,
                variant = BsbAccordionVariant.Plain
            ) {
                Text("Content inside plain accordion")
            }
        }
    }
}
