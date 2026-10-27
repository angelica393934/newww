package com.nbs.banking.components.atom.spacer

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun ColumnScope.Spacer(height: Dp) {
    Spacer(Modifier.height(height))
}

@Composable
fun ColumnScope.WeightedSpacer(weight: Float = 1f) {
    Spacer(Modifier.weight(weight))
}

@Composable
fun RowScope.Spacer(width: Dp) {
    Spacer(Modifier.width(width))
}

@Composable
fun RowScope.WeightedSpacer(weight: Float = 1f) {
    Spacer(Modifier.weight(weight))
}