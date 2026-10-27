package com.nbs.banking.core.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.nbs.banking.core.extensions.emptyString
import com.nbs.banking.core.extensions.toDp
import com.nbs.banking.core.extensions.withIo
import com.nbs.banking.core.extensions.withMain
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber

private const val BYTE_1024 = 1024

/**
 * Capture the given composable as a bitmap
 *
 * @param content @Composable (Modifier, (Int, Int) -> Unit) -> Unit
 * @param onImageCaptured (Bitmap) -> Unit
 * @return Unit
 *
 */
@Deprecated(
    message = "Use CaptureComposableAsBitmapV2 instead",
    replaceWith = ReplaceWith(
        expression = "CaptureComposableAsBitmapV2(content, onImageCaptured)",
        imports = ["com.danamon.mobile.core.utils.CaptureComposableAsBitmapV2"],
    ),
)
@Composable
fun CaptureComposableAsBitmap(content: @Composable (Modifier, (Int, Int) -> Unit) -> Unit, onImageCaptured: (Bitmap) -> Unit) {
    val scope = rememberCoroutineScope()
    var isCapturing by remember { mutableStateOf(false) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    val graphicsLayer = rememberGraphicsLayer()

    // This Layout makes sure it's NOT contributing size
    Box(
        modifier = Modifier
            .alpha(0f)
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    graphicsLayer.record(size = contentSize) {
                        this@onDrawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                }
            },
    ) {
        content(Modifier) { width, height ->
            contentSize = IntSize(width, height)
        }
    }

    LaunchedEffect(isCapturing) {
        if (isCapturing) {
            withIo {
                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                onImageCaptured(bitmap)
                isCapturing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        isCapturing = true
    }
}

/**
 * Captures a given composable as a Bitmap.
 * This version uses an AndroidView with a ComposeView to render the content off-screen
 * and then captures it. This approach might be more reliable for complex composables
 * or when specific view-based interactions are needed before capture, though it
 * might have a slight performance overhead compared to direct layer capture.
 *
 * The composable is rendered invisibly to the user by setting its alpha to 0f.
 * The capture process is launched in a CoroutineScope and the resulting Bitmap
 * is provided via the `onImageCaptured` callback. If the capture fails
 * (e.g., due to an IllegalStateException when drawing to the bitmap),
 * the `onImageCaptured` callback will be invoked with `null`.
 *
 * Example usage:
 * ```
 * var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
 *
 * CaptureComposableAsBitmapV2(
 *     content = {
 *         // Your composable content to be captured
 *         Text("Hello, World!")
 *     },
 *     onImageCaptured = { bitmap ->
 *         capturedBitmap = bitmap
 *         // Handle the captured bitmap (e.g., display it, save it)
 *     }
 * )
 * ```
 *
 * @param content The composable function to be captured.
 * @param onImageCaptured A callback function that will be invoked with the captured Bitmap.
 *                        This will be `null` if the capture process fails.
 * @param modifier An optional [Modifier] to be applied to the invisible Box that wraps the content
 *                 during capture. This can be used to control the size of the captured area if
 *                 the content itself doesn't define its own size constraints.
 */
@Composable
fun CaptureComposableAsBitmapV2(
    content: @Composable () -> Unit,
    onImageCaptured: (Bitmap?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val composeView = remember { ComposeView(context) }
    var hasCaptured by remember { mutableStateOf(false) }

    // Use LocalDensity to convert dp to px
    val density = LocalDensity.current
    val screenWidthPx = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.roundToPx()
    }

    AndroidView(
        factory = {
            composeView.apply {
                layoutParams = ViewGroup.LayoutParams(
                    screenWidthPx,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                visibility = View.INVISIBLE // Make the view invisible to the user
                setContent {
                    Box(
                        modifier = modifier
                            .width(screenWidthPx.toDp())
                            .wrapContentHeight(),
                    ) {
                        content()
                    }
                }
            }
        },
        modifier = Modifier.width(screenWidthPx.toDp())
            .wrapContentHeight(),
    )

    LaunchedEffect(Unit) {
        if (!hasCaptured) {
            delay(500L) // Wait for the ComposeView to be laid out
            Timber.i("Capturing composable as bitmap..")
            hasCaptured = true
            withMain {
                try {
                    Timber.i("Drawing ComposeView to bitmap")
                    val bitmap = composeView.drawToBitmap()
                    Timber.i("Bitmap captured successfully. Size: ${bitmap.width}x${bitmap.height}")
                    onImageCaptured(bitmap)
                } catch (e: IllegalStateException) {
                    Timber.e(e, "Bitmap capture failed")
                    onImageCaptured(null)
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, "Bitmap capture failed")
                    onImageCaptured(null)
                }
            }
        }
    }
}

/**
 * Save the given bitmap to the gallery
 *
 * @param context Context
 * @param fileName String
 * @param timestamp String
 * @param type MimeType
 * @param onErrorResult (Boolean) -> Unit
 * @return Unit
 */
suspend fun Bitmap?.saveToGallery(
    context: Context,
    fileName: String,
    timestamp: String,
    type: MimeType = MimeType.PNG,
    onErrorResult: (Boolean) -> Unit = {},
): Uri? {
    val saveToGalleryLabel = "saveToGallery"
    Timber.tag(saveToGalleryLabel).i("Saving bitmap to gallery")
    if (this == null) {
        Timber.tag(saveToGalleryLabel).e("Bitmap is null")
        onErrorResult(true)
        return null
    }
    val mimeType = type.name
    val filePath = try {
        context.getPicturesDirectory()
    } catch (e: Exception) {
        onErrorResult(true)
        Timber.tag(saveToGalleryLabel).e("Error getting pictures directory: ${e.message}")
        return null
    }
    val file = File(filePath, "${context.getAppName()}_receipt_$timestamp@$fileName.${mimeType.lowercase()}")
    Timber.tag(saveToGalleryLabel).i("File path: ${file.absolutePath}")

    var isError = false
    file.writeBitmap(this, type) { error ->
        isError = error
    }
    Timber.tag(saveToGalleryLabel).i("Wrote bitmap to file")

    if (isError) {
        onErrorResult(true)
        return null
    }

    return try {
        val uri = context.checkFilePath(file.absolutePath, type)
        onErrorResult(false) // Only call after scan is complete
        uri
    } catch (e: Exception) {
        onErrorResult(true)
        Timber.tag(saveToGalleryLabel).e("Error saving bitmap to gallery: ${e.message}")
        null
    }
}

fun File.writeBitmap(
    bitmap: Bitmap,
    mimeType: MimeType,
    initialQuality: Int = 100,
    qualityReductionStep: Int = 25,
    maxSizeInKB: Int = 150,
    onErrorResult: (Boolean) -> Unit = {},
) {
    val writeBitmapLabel = "writeBitmap"
    Timber.tag(writeBitmapLabel).i("Writing bitmap to file with initial quality $initialQuality")
    var quality = initialQuality
    val compressFormat = mimeType.compressFormat

    try {
        do {
            FileOutputStream(this).use { out ->
                bitmap.compress(compressFormat, quality, out)
                out.flush() // Ensure data is written
            }
            // Check if file size is greater than maxSizeInKB, reduce quality by qualityReductionStep for next iteration
            if (this.length() > maxSizeInKB * BYTE_1024 && quality > qualityReductionStep) { // Ensure quality doesn't go below the reduction step
                quality -= qualityReductionStep
                val isDeleted = this.delete() // Delete the oversized file
                if (isDeleted) {
                    Timber.tag(writeBitmapLabel).i("Sucessfully delete the file")
                } else {
                    Timber.tag(writeBitmapLabel).i("Unsucessfully delete the file")
                }
                Timber.tag(writeBitmapLabel).i("Reducing quality to $quality due to file size constraint")
            } else {
                break // File size is within limits or quality too low, stop compression loop
            }
        } while (true)
        Timber.tag(writeBitmapLabel).i("Bitmap written to file with final quality $quality")
        onErrorResult(false)
    } catch (e: FileNotFoundException) {
        onErrorResult(true)
        Timber.tag(writeBitmapLabel).e(e)
    } catch (e: SecurityException) {
        onErrorResult(true)
        Timber.tag(writeBitmapLabel).e(e)
    } catch (e: IOException) {
        onErrorResult(true)
        Timber.tag(writeBitmapLabel).e(e)
    } catch (e: NullPointerException) {
        onErrorResult(true)
        Timber.tag(writeBitmapLabel).e(e)
    } catch (e: IllegalArgumentException) {
        onErrorResult(true)
        Timber.tag(writeBitmapLabel).e(e)
    }
}

/**
 * Saves a Bitmap to a temporary file and returns a Uri to the file.
 * @param context The application context.
 * @param fileName The name of the file to save the Bitmap to.
 * @param timestamp The current date in string format.
 * @param mimeType The MIME type of the file.
 *
 * @return The Uri pointing to the saved Bitmap file.
 */
fun Bitmap.toUri(
    context: Context,
    fileName: String,
    timestamp: String,
    mimeType: MimeType = MimeType.PNG,
    initialQuality: Int = 100,
    qualityReductionStep: Int = 10,
    maxSizeInKB: Int = 512,
    onErrorResult: (Boolean) -> Unit = {},
): Uri? {
    val bitmapToUri = "BitmapToUri"
    Timber.tag(bitmapToUri).i("Converting Bitmap to Uri")
    var quality = initialQuality
    val compressFormat = mimeType.compressFormat
    val file = File(context.cacheDir, "${context.getAppName()}_$timestamp@$fileName.${mimeType.name.lowercase()}")
    var isFailed = true
    try {
        do {
            // Create a temporary file in the app's cache directory
            FileOutputStream(file).use { out ->
                compress(compressFormat, quality, out)
                out.flush()
            }
            // Check if file size is greater than maxSizeInKB, reduce quality by qualityReductionStep for next iteration
            if (file.length() > maxSizeInKB * BYTE_1024 && quality > qualityReductionStep) { // Ensure quality doesn't go below the reduction step
                quality -= qualityReductionStep
                val isDeleted = file.delete() // Delete the oversized file
                if (isDeleted) {
                    Timber.tag(bitmapToUri).i("Sucessfully delete the file")
                } else {
                    Timber.tag(bitmapToUri).i("Unsucessfully delete the file")
                }
                Timber.tag(bitmapToUri).i("Reducing quality to $quality due to file size constraint")
            } else {
                break // File size is within limits or quality too low, stop compression loop
            }
        } while (true)
        isFailed = false
        onErrorResult(isFailed)
    } catch (e: FileNotFoundException) {
        onErrorResult(isFailed)
        Timber.tag(bitmapToUri).e(e)
    } catch (e: SecurityException) {
        onErrorResult(isFailed)
        Timber.tag(bitmapToUri).e(e)
    } catch (e: IOException) {
        onErrorResult(isFailed)
        Timber.tag(bitmapToUri).e(e)
    } catch (e: NullPointerException) {
        onErrorResult(isFailed)
        Timber.tag(bitmapToUri).e(e)
    } catch (e: IllegalArgumentException) {
        onErrorResult(isFailed)
        Timber.tag(bitmapToUri).e(e)
    }

    return if (!isFailed) {
        // Return a Uri using FileProvider
        Timber.tag(bitmapToUri).i("File path: ${file.absolutePath}")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } else {
        null
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun Context.checkFilePath(filePath: String, mimeType: MimeType): Uri? = suspendCancellableCoroutine { continuation ->
    MediaScannerConnection.scanFile(
        this,
        arrayOf(filePath),
        arrayOf(mimeType.value),
    ) { _, scannedUri ->
        if (scannedUri == null) {
            continuation.cancel(Exception("File $filePath could not be scanned"))
            Timber.tag("checkFilePath").e("File $filePath could not be scanned")
        } else {
            continuation.resume(scannedUri)
            Timber.tag("checkFilePath").i("File $filePath scanned")
        }
    }
}

fun Uri?.shareImage(
    context: Context,
    mimeType: MimeType = MimeType.PNG,
    message: String = emptyString(),
    onErrorResult: (Boolean) -> Unit = {},
) {
    try {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, this@shareImage)
            putExtra(Intent.EXTRA_TEXT, message)
            type = mimeType.value
        }

        val chooser = Intent.createChooser(shareIntent, "Share Image")
        val resInfoList = context.packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, this, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(chooser)
        onErrorResult(false)
    } catch (e: Exception) {
        onErrorResult(true)
        Timber.tag("shareImage").e("Error sharing image: ${e.message}")
        return
    }
}

enum class MimeType(
    val value: String,
    val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
) {
    PNG("image/png", Bitmap.CompressFormat.PNG),
    JPEG("image/jpeg", Bitmap.CompressFormat.JPEG),
    WEBP("image/webp", Bitmap.CompressFormat.WEBP),
    PDF("application/pdf"),
}
