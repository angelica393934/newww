package com.nbs.banking.core.extensions

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import timber.log.Timber
import androidx.core.graphics.scale
import androidx.core.net.toUri

fun Bitmap.rotate(degrees: Float): Bitmap {
    val x: Int = this.width
    val y: Int = this.height
    val matrix = Matrix().apply { preRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, x, y, matrix, true)
}

fun Bitmap.toByteArray(compression: Int = 75): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, compression, stream)
    return stream.toByteArray()
}

fun Bitmap.encodeToBase64(targetCompression: Int = 75, targetSize: Int = 1_000_000): String {
    var compression = targetCompression
    var byteArray = this.toByteArray(compression)
    try {
        while (byteArray.size > targetSize) {
            compression -= 5
            byteArray = this.toByteArray(compression)
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP).orEmpty()
    } catch (e: UnsupportedEncodingException) {
        Timber.tag("BITMAP_TO_BASE64").e("$e")
        return ""
    }
}

/**
 * Compresses the given bitmap image to a desired file size and encodes it as a Base64 string.
 *
 * This function attempts to reduce the size of the image by adjusting the compression quality.
 * It starts with the maximum quality (100) and iteratively reduces the quality until the image
 * is compressed to a size that is equal to or less than the specified target size in bytes.
 *
 * The compression process is done using JPEG format, as it allows for adjustable quality levels.
 * The resulting compressed image is then encoded into a Base64 string representation.
 *
 * @param imageSizeBytes The desired maximum size of the image in bytes.
 *                       The function will compress the image until it reaches this size.
 *                       If the original image is already smaller than this, no further compression is done.
 * @return The Base64 encoded string of the compressed image. If compression fails, an empty string is returned.
 */
fun Bitmap.compressAndEncodeToBase64(imageSizeBytes: Int): String {
    var compressionQuality = 100
    var byteArray: ByteArray
    val byteArrayOutputStream = ByteArrayOutputStream()

    do {
        byteArrayOutputStream.reset()
        this.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream)
        byteArray = byteArrayOutputStream.toByteArray()
        compressionQuality -= 5
    } while (byteArray.size > imageSizeBytes && compressionQuality > 0)

    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun Bitmap.getBitmapSizeInBytes(): Int = this.byteCount

fun Bitmap.getResizedBitmap(maxSize: Int): Bitmap {
    val originalSize = 1_000_000

    if (this.getBitmapSizeInBytes() <= originalSize) return this

    var width = this.width
    var height = this.height

    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return this.scale(width, height)
}

fun String.stringToUri(): Uri = this.toUri()

fun <T> T?.isNull(): Boolean = this == null

fun <T> T?.isNotNull(): Boolean = this != null

fun <T> MutableList<T>?.orEmpty(): MutableList<T> = this ?: mutableListOf()

fun Char.isNotText(): Boolean = this.isDigit() or !this.isLetterOrDigit()

fun Unit?.orEmpty() = this ?: Unit

fun (() -> Unit)?.orEmpty() = this ?: {}

fun Any?.ifNotInt(default: Int) = this as? Int ?: default

fun emptyAction(): () -> Unit = {}

fun Any?.toStringMapOrNull(): Map<String, String?>? = (this as? Map<*, *>)
    ?.mapNotNull { (k, v) ->
        (k as? String)?.let { key ->
            key to v as? String
        }
    }
    ?.toMap()
