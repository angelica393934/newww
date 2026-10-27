package com.nbs.banking.navigation.utils

import android.net.Uri

fun Uri.getQueryParameterString(key: String): String = getQueryParameter(key).orEmpty().trim()

fun Uri.getQueryParameterStringOrNull(key: String): String? = getQueryParameter(key)?.trim()

fun Uri.getQueryParameterInt(key: String): Int = getQueryParameter(key).orEmpty().trim().ifEmpty { "0" }.toDouble().toInt()

fun Uri.getQueryParameterIntOrNull(key: String): Int? = getQueryParameter(key).orEmpty().trim().ifEmpty { "0" }.toDoubleOrNull()?.toInt()

fun Uri.getQueryParameterDouble(key: String): Double = getQueryParameter(key).orEmpty().trim().ifEmpty { "0.0" }.toDouble()

fun Uri.getQueryParameterBoolean(key: String): Boolean = getQueryParameter(key).orEmpty().trim().equals("true", ignoreCase = true)

fun Uri.getQueryParameterBooleanOrNull(key: String): Boolean? {
    val value: String? = getQueryParameter(key)?.trim()
    return value?.equals("true", ignoreCase = true)
}

fun Uri?.orEmpty(): Uri = this ?: Uri.EMPTY

fun Uri.replaceQueryParameter(key: String, newValue: String): Uri {
    val builder = buildUpon().clearQuery()
    queryParameterNames.forEach { param ->
        val value = if (param == key) newValue else getQueryParameter(param)
        builder.appendQueryParameter(param, value)
    }
    return builder.build()
}
