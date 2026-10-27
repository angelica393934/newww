package com.nbs.banking.navigation.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

fun slideInFromRight() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(200, easing = FastOutSlowInEasing),
)

fun slideOutToLeft() = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(200, easing = FastOutSlowInEasing),
)

fun slideInFromLeft() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(200, easing = FastOutSlowInEasing),
)

fun slideOutToRight() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(200, easing = FastOutSlowInEasing),
)
