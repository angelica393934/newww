package com.nbs.banking.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetricsCalculator
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

/**
 * Returns true only when the device is a foldable open (flat) or half-open,
 * and false when it’s fully closed or not a foldable device at all.
 *
 * - FULL OPEN (flat hinge): true
 * - HALF-OPENED (book mode): true
 * - FULLY CLOSED (no hinge feature reported): false
 * - NON-FOLDABLE (no hinge feature reported): false
 */
@SuppressLint("RestrictedApi")
@Composable
fun rememberFoldedScreenState(): Boolean {
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity } ?: return false

    val lifecycleOwner = LocalContext.current as? LifecycleOwner
    val windowLayoutInfoFlow = WindowInfoTracker
        .getOrCreate(activity)
        .windowLayoutInfo(activity)
        .let { flow ->
            lifecycleOwner
                ?.lifecycle
                ?.let { flow.flowWithLifecycle(it, Lifecycle.State.STARTED) }
                ?: flow
        }

    val layoutInfo by windowLayoutInfoFlow
        .collectAsState(initial = WindowLayoutInfo(emptyList()))

    val foldingFeature = layoutInfo.displayFeatures
        .filterIsInstance<FoldingFeature>()
        .firstOrNull()

    // When feature != null, state is either HALF_OPENED or FLAT → show large
    return foldingFeature != null
}

/**
 * Continuously observes and returns the **current window width** in [Dp],
 * updating whenever the window bounds change (e.g., due to fold/unfold,
 * rotation, or split-screen resize).
 *
 * This function uses [WindowMetricsCalculator] to compute the *actual*
 * window width in real time, rather than relying on static configuration
 * values like [android.content.res.Configuration.screenWidthDp].
 *
 * Designed to work on all modern devices including foldables and tablets,
 * even when configuration updates are suppressed (Android 12L+).
 *
 * Example usage:
 * ```
 * val screenWidth = rememberLiveScreenWidthDp()
 * val isSinglePane = screenWidth < 600.dp
 * val isDualPane = screenWidth >= 840.dp
 * ```
 *
 * @return The current **window width** in [Dp], updated roughly every 500ms.
 */
@Composable
fun rememberLiveScreenWidthDp(): Dp {
    val context = LocalContext.current
    val resources = LocalResources.current
    val density = LocalDensity.current
    var widthDp by remember { mutableStateOf(0.dp) }

    LaunchedEffect(Unit) {
        val displayMetrics = resources.displayMetrics
        var lastWidth = 0f

        while (true) {
            val metrics = WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(context)

            val newWidthPx = metrics.bounds.width().toFloat()
            val newWidthDpValue = newWidthPx / displayMetrics.density

            if (newWidthDpValue != lastWidth) {
                lastWidth = newWidthDpValue
                widthDp = with(density) { newWidthPx.toDp() }
                Timber.i("LiveScreenWidthLOG -> screenWidthDp: $newWidthDpValue")
            }

            delay(500L.milliseconds)
        }
    }

    return widthDp
}
