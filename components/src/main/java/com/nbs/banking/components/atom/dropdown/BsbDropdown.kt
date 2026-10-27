package com.nbs.banking.components.atom.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.R
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

enum class BsbDropdownVariant {
    OUTLINED,
    TEXT
}

@Composable
fun BsbDropdown(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Dropdown",
    variant: BsbDropdownVariant = BsbDropdownVariant.OUTLINED,
    trailingText: String? = null,
    enabled: Boolean = true,
    highlighted: Boolean = false
){
    val colors = Bsb.Color.baseColor
    var expanded by remember { mutableStateOf(false) }
    val outlined = variant == BsbDropdownVariant.OUTLINED
    val active = enabled && (highlighted || expanded)
    val borderColor = if (active) colors.primary2 else colors.gray200
    val valueColor = when {
        !enabled -> colors.gray200
        selectedOption != null || !outlined -> colors.gray950
        else -> colors.gray400
    }
    val arrowColor = when {
        !enabled -> colors.gray200
        active -> colors.primary2
        outlined -> colors.gray200
        else -> colors.gray950
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_8)
    ) {
        if (label != null) {
            Text(
                text = label,
                style = Bsb.Font.button2.copy(fontWeight = FontWeight.Normal),
                color = colors.gray950,
                modifier = Modifier.padding(start = Dimension.SIZE_16)
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Dimension.SIZE_48),
                shape = RoundedCornerShape(Dimension.SIZE_100),
                color = Color.Transparent,
                border = if (outlined) {
                    BorderStroke(Dimension.SIZE_1, borderColor)
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = enabled,
                            role = Role.Button
                        ) {
                            expanded = !expanded
                        }
                        .then(
                            if (outlined) Modifier.padding(horizontal = Dimension.SIZE_24)
                            else Modifier
                        )
                        .padding(vertical = Dimension.SIZE_12),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimension.SIZE_12)
                ) {
                    Text(
                        text = selectedOption ?: placeholder,
                        modifier = Modifier.weight(1f),
                        style = Bsb.Font.button2.copy(fontWeight = FontWeight.SemiBold),
                        color = valueColor
                    )

                    if (trailingText != null) {
                        Text(
                            text = trailingText,
                            style = Bsb.Font.button2.copy(fontWeight = FontWeight.Normal),
                            color = if (enabled) colors.gray950 else colors.gray200
                        )
                    }


                    Icon(
                        painter = painterResource(
                            id = if (expanded) {
                                R.drawable.ic_chevron_up
                            } else {
                                R.drawable.ic_chevron_down
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(Dimension.SIZE_24),
                        tint = arrowColor
                    )
                }
            }


            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = Bsb.Font.button2,
                                color = colors.gray950
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BsbDropdownArrow(
    expanded: Boolean,
    color: Color
) {
    Canvas(modifier = Modifier.size(Dimension.SIZE_24)) {
        val ySide = if (expanded) 0.62f else 0.38f
        val yCenter = if (expanded) 0.42f else 0.58f
        val stroke = Dimension.SIZE_1.toPx() * 3f

        drawLine(
            color = color,
            start = Offset(size.width * 0.28f, size.height * ySide),
            end = Offset(size.width * 0.5f, size.height * yCenter),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * yCenter),
            end = Offset(size.width * 0.72f, size.height * ySide),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BsbDropdownPreview() {
    val options = listOf("Placeholder", "Dropdown", "Option 1", "Option 2")
    var highlightedSelection by remember { mutableStateOf<String?>("Dropdown") }
    var outlinedSelection by remember { mutableStateOf<String?>("Placeholder") }
    var firstTextSelection by remember { mutableStateOf<String?>("Placeholder") }
    var secondTextSelection by remember { mutableStateOf<String?>("Placeholder") }

    Surface {
        Column(
            modifier = Modifier.padding(Dimension.SIZE_16),
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_24)
        ) {
            BsbDropdown(
                label = "Text",
                options = options,
                selectedOption = null,
                onOptionSelected = {},
                enabled = false
            )

            BsbDropdown(
                label = "Text",
                options = options,
                selectedOption = highlightedSelection,
                onOptionSelected = { highlightedSelection = it },
                highlighted = true
            )

            BsbDropdown(
                label = "Text",
                options = options,
                selectedOption = outlinedSelection,
                onOptionSelected = { outlinedSelection = it },
                trailingText = "Placeholder"
            )

            BsbDropdown(
                options = options,
                selectedOption = firstTextSelection,
                onOptionSelected = { firstTextSelection = it },
                variant = BsbDropdownVariant.TEXT
            )

            BsbDropdown(
                options = options,
                selectedOption = secondTextSelection,
                onOptionSelected = { secondTextSelection = it },
                variant = BsbDropdownVariant.TEXT
            )
        }
    }
}