package com.nbs.banking.core.utils

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

class JsonLoader @Inject constructor(private val context: Context) {
    /**
     * Load JSON file from the assets folder
     *
     * @param path The relative path of the file in the assets folder.
     * @return The content of the JSON file as a String.
     * @throws IOException If there is an error reading the file.
     */
    fun loadJsonFromAssets(path: String): String = try {
        val inputStream = context.assets.open(path)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.use { it.readText() }
        json
    } catch (e: IOException) {
        throw IOException("Error reading JSON file from assets at path: $path", e)
    }
}
