package com.nbs.banking.core.extensions

import android.os.Bundle
import timber.log.Timber

fun Map<String, Any?>.toBundle(): Bundle = Bundle().apply {
    forEach { (key, value) ->
        when (value) {
            is String -> {
                putString(key, value)
            }

            is Int -> {
                putInt(key, value)
            }

            is Float -> {
                putFloat(key, value)
            }

            is Boolean -> {
                putBoolean(key, value)
            }

            is Double -> {
                putDouble(key, value)
            }

            is Char -> {
                putChar(key, value)
            }

            is Short -> {
                putShort(key, value)
            }

            is Byte -> {
                putByte(key, value)
            }

            is Long -> {
                putLong(key, value)
            }

            else -> {
                Timber.e(IllegalArgumentException("Unsupported type for Bundle: $key -> $value"))
                putString(key, value.toString())
            }
        }
    }
}
