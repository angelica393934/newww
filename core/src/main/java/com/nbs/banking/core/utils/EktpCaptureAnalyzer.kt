package com.nbs.banking.core.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import timber.log.Timber

class EktpCaptureAnalyzer(
    private val onFrameAnalyzed: (ImageProxy) -> Unit = {},
) : ImageAnalysis.Analyzer {
    @Volatile
    var isPaused: Boolean = true

    override fun analyze(imageProxy: ImageProxy) {
        if (isPaused) {
            imageProxy.close()
            return
        }

        runCatching {
            onFrameAnalyzed(imageProxy)
        }.onFailure {
            Timber.e(it.stackTraceToString())
            imageProxy.close()
            throw it
        }
    }
}
