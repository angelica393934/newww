package com.nbs.banking.components.molecule.selectable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.nbs.banking.components.theme.Bsb
import com.nbs.banking.components.theme.primitives.Dimension

data class SelectableItem(
    val id: String,
    val text: String
)

@Composable
fun BsbSelectableItem(
    modifier: Modifier = Modifier,
    item: SelectableItem,
    isSelected: Boolean,
    onClick: (item: SelectableItem) -> Unit,
) {
    val backgroundColor = if (isSelected) Bsb.Color.baseColor.primary8 else Color.Transparent
    val borderColor = if (isSelected) Bsb.Color.baseColor.primary1 else Bsb.Color.baseColor.gray200
    val textColor = if (isSelected) Bsb.Color.baseColor.primary1 else Bsb.Color.baseColor.gray950

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimension.SIZE_20))
            .border(
                width = Dimension.SIZE_1,
                color = borderColor,
                shape = RoundedCornerShape(Dimension.SIZE_20)
            )
            .background(backgroundColor)
            .clickable(onClick = {
                onClick(item)
            })
            .padding(Dimension.SIZE_16),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.text,
            style = Bsb.Font.text6,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun BsbSelectableItemPreview() {
    Surface(modifier = Modifier.padding(Dimension.SIZE_16)) {
        Column {
            BsbSelectableItem(
                item = SelectableItem(id = "1", text = "Investasi"),
                isSelected = false,
                onClick = {}
            )
            Spacer(modifier = Modifier.height(Dimension.SIZE_16))
            BsbSelectableItem(
                item = SelectableItem(id = "2", text = "Investasi"),
                isSelected = true,
                onClick = {}
            )
        }
    }
}
