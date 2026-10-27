package com.nbs.banking.core.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.WindowManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import timber.log.Timber

private const val DEFAULT_BARCODE_SIZE = 400

fun generateBarcode(
    data: String,
    widthPixel: Int = DEFAULT_BARCODE_SIZE,
    heightPixel: Int = DEFAULT_BARCODE_SIZE,
    barcodeFormat: BarcodeFormat = BarcodeFormat.CODE_39,
): Bitmap? {
    try {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap: Bitmap = barcodeEncoder.encodeBitmap(data, barcodeFormat, widthPixel, heightPixel)
        return bitmap
    } catch (e: WriterException) {
        Timber.d("Error generating barcode: $e")
        return null
    }
}

fun setBrightness(context: Context, isFull: Boolean) {
    val activity = context as? Activity ?: return
    val layoutParams: WindowManager.LayoutParams = activity.window.attributes
    layoutParams.screenBrightness = if (isFull) 1.0f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    activity.window.attributes = layoutParams
}
