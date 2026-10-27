package com.nbs.banking.components.atom.switchs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

@Composable
fun BsbSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = Bsb.Color.baseColor
    val trackColor by animateColorAsState(
        targetValue = if (checked) colors.primary7 else colors.gray100,
        label = "BsbSwitchTrackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) colors.primary2 else colors.gray300,
        label = "BsbSwitchThumbColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) (Dimension.SIZE_80 - Dimension.SIZE_36 - Dimension.SIZE_6) else Dimension.SIZE_6,
        label = "BsbSwitchThumbOffset"
    )

    Box(
        modifier = modifier
            .size(width = Dimension.SIZE_80, height = Dimension.SIZE_48)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(Dimension.SIZE_100))
            .background(trackColor)
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(Dimension.SIZE_36)
                .background(thumbColor, CircleShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BsbSwitchPreview() {
    Surface {
        Column(
            modifier = Modifier.padding(Dimension.SIZE_24),
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_24)
        ) {
            BsbSwitch(checked = false, onCheckedChange = {})
            BsbSwitch(checked = true, onCheckedChange = {})
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BsbSwitchInteractivePreview() {
    var firstChecked by remember { mutableStateOf(false) }
    var secondChecked by remember { mutableStateOf(true) }

    Surface {
        Column(
            modifier = Modifier.padding(Dimension.SIZE_24),
            verticalArrangement = Arrangement.spacedBy(Dimension.SIZE_24)
        ) {
            BsbSwitch(
                checked = firstChecked,
                onCheckedChange = { firstChecked = it }
            )

            BsbSwitch(
                checked = secondChecked,
                onCheckedChange = { secondChecked = it }
            )
        }
    }
}
