package com.nbs.banking.core.utils

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import com.nbs.banking.core.BuildConfig
import com.nbs.banking.core.extensions.getActivity

/**
 * Window flag: treat the content of the window as secure,
 * preventing it from appearing in screenshots or from being viewed on non-secure displays.
 */
fun Context.enableSecureFlag(isEnabled: Boolean) {
    if (this !is Activity) return
    if (!BuildConfig.DEBUG) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
        )
    }
}

fun Context.isSecureFlagEnabled(): Boolean {
    if (this !is Activity) return false
    val flags = window.attributes.flags
    return flags and WindowManager.LayoutParams.FLAG_SECURE != 0
}

// Note: Bottom sheet window is separate from the main window
// As such, it may not link to the main window's secure flag
// this function passes a value to sync up the main window and bottom sheet.
fun enableBottomSheetSecureFlag(context: Context, isEnabled: Boolean) {
    if (!BuildConfig.DEBUG) {
        context.getActivity()?.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
    } else {
        context.getActivity()?.window!!.clearFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
        )
    }
}
