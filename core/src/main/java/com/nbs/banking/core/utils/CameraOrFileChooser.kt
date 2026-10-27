package com.nbs.banking.core.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher

fun launchCameraOrFileChooser(
    launcher: ActivityResultLauncher<Intent>,
    isCameraGranted: Boolean,
    capturedImageUri: Uri,
    allowedMimeTypes: Array<String>,
    chooserTitle: String = "Select Action",
) {
    val intentList = mutableListOf<Intent>()

    // Set up the camera intent with the file URI
    val camIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri) // Pass URI to capture full-size image
    }

    // Set up the file picker intent
    val fileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "*/*"
        putExtra(Intent.EXTRA_MIME_TYPES, allowedMimeTypes)
    }

    // Add intents based on permissions
    if (isCameraGranted) {
        intentList.add(camIntent)
    }
    intentList.add(fileIntent)

    // Create and launch the chooser intent
    val chooserIntent = Intent.createChooser(Intent(), chooserTitle)
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toTypedArray())
    launcher.launch(chooserIntent)
}
