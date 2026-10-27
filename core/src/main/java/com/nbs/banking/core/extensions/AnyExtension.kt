package com.nbs.banking.core.extensions

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.FileProvider
import com.nbs.banking.core.utils.Constants
import java.io.File
import java.math.BigDecimal
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

fun <T> Result<List<T>?>.getOrEmpty(): List<T> = getOrNull().orEmpty()

fun <T : Map<String, Any?>> Result<T?>.getOrEmpty(): Map<String, Any?> = getOrNull().orEmpty()

fun <T : HashMap<String, Any?>> Result<T?>.getOrEmpty(): HashMap<String, Any?> = getOrNull() ?: hashMapOf()

fun Result<String?>.getOrEmpty(): String = getOrNull().orEmpty()

fun Result<BigDecimal?>.getOrZero(): BigDecimal = getOrNull().orZero()

inline fun <reified T : Any> T?.nullIfAllPropertiesNull(): T? {
    if (this == null) return null
    val allNull = T::class.memberProperties
        .all { it.get(this) == null }
    return if (allNull) null else this
}

fun Context.safeReadFile(fileName: String): String? = runCatching {
    assets.open(fileName).bufferedReader().use {
        it.readText()
    }
}.onFailure { e ->
    Timber.e(e)
}.getOrNull()

fun Context.createTempPictureUri(
    provider: String = "$packageName.provider",
    fileName: String = "picture_${System.currentTimeMillis()}",
    fileExtension: String = Constants.JPG_EXTENSION_TYPE,
): Uri {
    val tempFile = File.createTempFile(
        fileName,
        fileExtension,
        cacheDir,
    ).apply {
        createNewFile()
    }

    return FileProvider.getUriForFile(applicationContext, provider, tempFile)
}

@Composable
inline fun OneTimeEffect(crossinline onFirstTime: suspend CoroutineScope.() -> Unit) {
    var isFirstTime by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isFirstTime) {
            isFirstTime = false
            onFirstTime()
        }
    }
}

/**
 * Epoch-based countdown timer.
 *
 * Calculates remaining time using System.currentTimeMillis, so the result stays accurate
 * even when the app goes to background or experiences delays.
 *
 * Preferred over CountDownTimer for business-critical deadlines
 * (e.g. transaction expiry, confirmation timeout).
 */
@Composable
fun BackgroundTimerEffect(
    expiryEpochMillis: Long,
    shouldRun: Boolean = true,
    onTick: (millisUntilFinished: Long) -> Unit,
    onFinish: () -> Unit = {},
    intervalMillis: Long = 1_000L,
) {
    LaunchedEffect(expiryEpochMillis, shouldRun) {
        if (!shouldRun) return@LaunchedEffect

        while (true) {
            val remaining = expiryEpochMillis - System.currentTimeMillis()

            if (remaining <= 0L) {
                onTick(0L)
                onFinish()
                break
            }

            onTick(remaining)
            delay(intervalMillis)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getBottomSheetStates() = rememberCoroutineScope() to rememberModalBottomSheetState(skipPartiallyExpanded = true)

@Composable
fun TimerEffect(
    expiryDate: Long = zeroLong(),
    explicitMillis: Long = zeroLong(),
    explicitDuration: Long = zeroLong(),
    shouldRun: Boolean = true,
    onTick: (millisUntilFinished: Long) -> Unit,
    onFinish: () -> Unit = {},
    countdownInterval: Long = 1_000L,
) {
    var countDownTimer: CountDownTimer? by remember { mutableStateOf(null) }

    // Cancel timer when composable leaves composition
    DisposableEffect(Unit) {
        countDownTimer?.cancel()

        if (shouldRun) {
            val nowMillis = System.currentTimeMillis()
            val expiryMillis = if (explicitMillis.isNotZero()) explicitMillis else expiryDate
            val timerDuration = if (explicitDuration.isNotZero()) explicitDuration else expiryMillis - nowMillis

            countDownTimer = object : CountDownTimer(timerDuration, countdownInterval) {
                override fun onTick(millisUntilFinished: Long) {
                    onTick(millisUntilFinished)
                }

                override fun onFinish() {
                    onFinish()
                }
            }.apply { start() }
        }
        onDispose {
            countDownTimer?.cancel()
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.showBottomSheet(scope: CoroutineScope, onShow: () -> Unit) {
    scope.launch {
        show()
    }.invokeOnCompletion {
        if (isVisible) {
            onShow()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.hideBottomSheet(scope: CoroutineScope, onHidden: () -> Unit) {
    scope.launch {
        hide()
    }.invokeOnCompletion {
        if (!isVisible) {
            onHidden()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.closeBottomSheet(scope: CoroutineScope, onHidden: () -> Unit) {
    scope.launch {
        hide()
        delay(100L)
        onHidden()
    }
}

inline fun <reified T> canonicalName(): String? = T::class.java.canonicalName

//@Composable
//inline fun <reified T : ViewModel> getOrNewViewModelStoreOwner() = run {
//    val composeView = LocalView.current
//    val composeViewModelStoreOwner = composeView.findViewTreeViewModelStoreOwner()
//    composeViewModelStoreOwner?.let { hiltViewModel<T>(it) } ?: run { hiltViewModel<T>() }
//}

fun <T> T?.orDefault(default: T): T = this ?: default

fun List<Any>.getLastIndexOfDuplicates(): Int? {
    val lastIndexMap = mutableMapOf<Any, Int>()
    val duplicateIndices = mutableListOf<Int>()

    this.forEachIndexed { index, item ->
        if (lastIndexMap.containsKey(item)) {
            lastIndexMap[item]?.let { duplicateIndices.add(it) }
            duplicateIndices.add(index)
        }
        lastIndexMap[item] = index
    }

    return duplicateIndices.lastOrNull()
}

@Composable
fun <T> T.setDebounce(
    delayMillis: Long = 500L,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onChange: (T) -> Unit,
): T {
    val state by rememberUpdatedState(this)

    DisposableEffect(state) {
        val job = coroutineScope.launch {
            delay(delayMillis)
            onChange(state)
        }
        onDispose {
            job.cancel()
        }
    }
    return state
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun String?.copyToClipboard(clipboardManager: ClipboardManager) {
    clipboardManager.setText(AnnotatedString(this.orEmpty()))
}

@Suppress("DEPRECATION")
fun <T> Intent.getParcelableExtraCompat(tag: String, clazz: Class<T>): T? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    getParcelableExtra(tag, clazz)
} else {
    getParcelableExtra(tag)
}

fun Bitmap.cropToScreenSize(height: Float, width: Float): Bitmap {
    val bitmapWidth = this.width.toFloat()
    val bitmapHeight = this.height.toFloat()

    // Calculate the target aspect ratio
    val targetRatio = width / height

    // Calculate new width and height
    val currentRatio = bitmapWidth / bitmapHeight
    val (newWidth, newHeight) = if (currentRatio > targetRatio) {
        // Bitmap is wider than the target ratio, adjust width
        val adjustedWidth = bitmapHeight * targetRatio
        adjustedWidth to bitmapHeight
    } else {
        // Bitmap is taller than the target ratio, adjust height
        val adjustedHeight = bitmapWidth / targetRatio
        bitmapWidth to adjustedHeight
    }

    // Calculate the cropping coordinates
    val x = (bitmapWidth - newWidth) / 2
    val y = (bitmapHeight - newHeight) / 2

    // Create and return the cropped bitmap
    return Bitmap.createBitmap(this, x.roundToInt(), y.roundToInt(), newWidth.roundToInt(), newHeight.roundToInt())
}
