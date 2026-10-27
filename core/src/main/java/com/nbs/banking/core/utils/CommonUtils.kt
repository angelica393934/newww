package com.nbs.banking.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.core.text.parseAsHtml
import coil3.Image
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.target.Target
import coil3.toBitmap
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.core.state.ScreenState.Error as ErrorState
import com.nbs.banking.core.utils.Constants.PDF_EXTENSION_TYPE
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

object CommonUtils {
    private const val TEMP_PDF = "temp_pdf"

    private val imageCache = mutableMapOf<String, ImageBitmap>()

    fun loadImageForNotification(
        context: Context,
        imageUrl: String,
        builder: NotificationCompat.Builder,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest
            .Builder(context)
            .data(imageUrl)
            .allowHardware(false) // Disable hardware bitmaps
            .target(
                object : Target {
                    override fun onSuccess(result: Image) {
                        val bitmap = result.toBitmap()
                        builder.setStyle(
                            NotificationCompat
                                .BigPictureStyle()
                                .bigPicture(bitmap),
                        )
                        onSuccess.invoke()
                    }

                    override fun onError(error: Image?) {
                        onError.invoke()
                    }
                },
            ).build()
        imageLoader.enqueue(request)
    }

    fun showToast(context: Context, text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun Context.copyTextToClipboard(text: String) {
        val clipboardManager = runCatching {
            this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        }.getOrNull()
        val clipData = ClipData.newPlainText("Text Copied", text)
        clipboardManager?.setPrimaryClip(clipData)
    }

    fun Context.openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        )
        startActivity(intent)
    }

    fun Context.openLocationSourceSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    fun Context.openDateTimeSetting() {
        val intent = Intent(Settings.ACTION_DATE_SETTINGS)
        startActivity(intent)
    }

    fun getFileResourceJson(context: Context, pathJson: String): String = context.assets.open(pathJson).bufferedReader().use {
        it.readText()
    }

    fun formatPascalCase(name: String): String = name.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun dial(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
        context.startActivity(intent)
    }

//    fun playStore(context: Context) {
//        val playStoreIntent = Intent(Intent.ACTION_VIEW, BuildConfig.STORE_URL.toUri())
//        context.startActivity(playStoreIntent)
//    }
//
//    fun playStore(context: Context, customLink: String) {
//        val uri = customLink.ifEmpty { BuildConfig.STORE_URL }.toUri()
//        val playStoreIntent = Intent(Intent.ACTION_VIEW, uri).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        }
//        context.startActivity(playStoreIntent)
//    }

    fun whatsapp(
        context: Context,
        phone: String,
        text: String,
        countryCode: String = "62",
    ) {
        var url = "https://api.whatsapp.com/send?phone="
        val formattedPhone = phone.filter { it.isDigit() }
        url += if (formattedPhone.startsWith(countryCode)) {
            formattedPhone
        } else {
            "$countryCode$formattedPhone"
        }
        if (text.isNotEmpty()) {
            url += "&text=${text.parseAsHtml()}"
        }
        if (isAppInstalled(context, "com.whatsapp")) {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } else {
            val playStoreIntent =
                Intent(Intent.ACTION_VIEW, "market://details?id=com.whatsapp".toUri())
            context.startActivity(playStoreIntent)
        }
    }

    fun email(context: Context, email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri())
        context.startActivity(intent)
    }

    fun openBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openPdf(
        context: Context,
        url: String,
        onLoadingChange: ((Boolean) -> Unit)? = null,
    ) {
        if (!hasPdfViewer(context)) {
            openBrowser(context, url)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            onLoadingChange?.invoke(true)

            val isUrlValid = withContext(Dispatchers.IO) {
                checkPdfUrl(url)
            }

            if (isUrlValid) {
                getPdfFromSource(url, context)?.also {
                    val uri =
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", it)
                    // Create an intent that can handle both PDFs and web pages
                    val pdfIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY)
                        addCategory(Intent.CATEGORY_BROWSABLE)
                    }
                    val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(url.toUri(), "*/*") // Let the browser handle the URL
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        addCategory(Intent.CATEGORY_BROWSABLE)
                    }

                    // Create a chooser to combine both intents
                    val chooser = Intent.createChooser(
                        pdfIntent,
                        "Open PDF with",
                    ).apply {
                        putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(browserIntent))
                    }
                    onLoadingChange?.invoke(false)
                    context.startActivity(chooser)
                }
            } else {
                onLoadingChange?.invoke(false)
                openBrowser(context, url)
            }
        }
    }

    private fun hasPdfViewer(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "application/pdf"
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }
        val resolveInfo =
            context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo != null
    }

    suspend fun checkPdfUrl(url: String): Boolean = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Range", "bytes=0-1") // Fetch only 1 byte to quickly validate PDF
            conn.connect()
            val responseCode = conn.responseCode
            Timber.d("PDF URL check response code for $url: $responseCode")
            // Consider 200-level responses as successful
            responseCode in 200..299
        } catch (e: IOException) {
            Timber.e(e, "Error checking PDF URL: $url")
            false // Network or other IO error
        } finally {
            connection?.disconnect()
        }
    }

    private suspend fun loadImageFromSource(input: String): ImageBitmap = withContext(Dispatchers.IO) {
        try {
            val bitmap = if (input.startsWith("http")) {
                // Handle URL
                val url = URL(input)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                BitmapFactory.decodeStream(inputStream)
            } else {
                // Handle Base64
                val imageBytes = Base64.decode(input, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }
            bitmap?.asImageBitmap() ?: createFallbackImage()
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            createFallbackImage()
        } catch (e: IOException) {
            Timber.e(e)
            createFallbackImage()
        } catch (e: SecurityException) {
            Timber.e(e)
            createFallbackImage()
        }
    }

    @Composable
    fun getImageFromSource(input: String): ImageBitmap {
        var imageBitmap by remember(input) { mutableStateOf(imageCache[input]) }

        LaunchedEffect(input) {
            if (!imageCache.containsKey(input)) {
                val loaded = loadImageFromSource(input)
                imageCache[input] = loaded
                imageBitmap = loaded
            }
        }

        return imageBitmap ?: createFallbackImage()
    }

    private fun createFallbackImage(): ImageBitmap {
        // Create 1x1 transparent pixel as fallback
        val fallbackBitmap = createBitmap(1, 1)
        fallbackBitmap.eraseColor(android.graphics.Color.TRANSPARENT)
        return fallbackBitmap.asImageBitmap()
    }

    suspend fun getPdfFromSource(source: String, context: Context): File? = withContext(Dispatchers.IO) {
        try {
            if (source.startsWith("http")) {
                // Handle URL
                val url = URL(source)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()

                val file = File.createTempFile(TEMP_PDF, PDF_EXTENSION_TYPE, context.cacheDir)
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                file
            } else {
                // Handle Base64
                val cleanBase64 = source.replace("data:application/pdf;base64,", "")
                val pdfBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val file = File.createTempFile(TEMP_PDF, PDF_EXTENSION_TYPE, context.cacheDir)
                file.writeBytes(pdfBytes)
                file
            }
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            null
        } catch (e: IOException) {
            Timber.e(e)
            null
        } catch (e: SecurityException) {
            Timber.e(e)
            null
        }
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        try {
            context.packageManager.getPackageInfo(packageName, 0)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
            return false
        }
    }

    fun isPdfFile(file: File?): Boolean {
        try {
            val inputStream = FileInputStream(file)
            val headerBytes = ByteArray(4)
            inputStream.read(headerBytes)
            inputStream.close()
            val headerString = String(headerBytes, Charsets.US_ASCII)
            return headerString == "%PDF"
        } catch (e: IOException) {
            Timber.e(e)
            return false
        }
    }

    fun File.shareFile(
        context: Context,
        title: String,
        @StringRes message: Int? = null,
        mimeType: MimeType = MimeType.PDF,
        onErrorResult: (Boolean) -> Unit = {},
    ) {
        try {
            val renamedFile = File(this.parent, "$title${PDF_EXTENSION_TYPE}")
            val fileToShare = if (this.name != renamedFile.name) {
                this.copyTo(renamedFile, overwrite = true)
            } else {
                this
            }
            val uri =
                FileProvider.getUriForFile(context, "${context.packageName}.provider", fileToShare)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, message?.let { context.getString(it) })
                type = mimeType.value
            }

            val chooser = Intent.createChooser(
                shareIntent,
                "Share File",
            )
            val resInfoList = context.packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY,
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            context.startActivity(chooser)
            onErrorResult(false)
        } catch (e: FileNotFoundException) {
            onErrorResult(true)
            Timber.e("File not found while sharing PDF: ${e.message}")
        } catch (e: IOException) {
            onErrorResult(true)
            Timber.e("I/O error while sharing PDF: ${e.message}")
        } catch (e: SecurityException) {
            onErrorResult(true)
            Timber.e("Security exception while sharing PDF: ${e.message}")
        }
    }

    fun pdfSaveAction(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, title: String) {
        val cleanTitle = if (title.contains('.')) { // to remove extra file extension
            title.substringBefore('.')
        } else {
            title
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MimeType.PDF.value
            putExtra(Intent.EXTRA_TITLE, cleanTitle + PDF_EXTENSION_TYPE)
        }
        launcher.launch(intent)
    }

    fun decodeBase64ToPdfUIState(base64String: String, context: Context): ScreenState<File> {
        val result = try {
            val cleanBase64 = base64String.replace("data:application/pdf;base64,", "")
            val pdfBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val file = File.createTempFile(TEMP_PDF, PDF_EXTENSION_TYPE, context.cacheDir)
            file.writeBytes(pdfBytes)
            if (!file.exists() || file.length() == 0L) {
                return ErrorState(IOException("Failed to decode and create PDF file"), "Empty")
            }
            ScreenState.Success(file)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            ErrorState(e, "Invalid Base64 string")
        } catch (e: IOException) {
            Timber.e(e)
            ErrorState(e, "Error writing file")
        } catch (e: SecurityException) {
            Timber.e(e)
            ErrorState(e, "Security exception, cannot write file")
        }
        return result
    }

    fun clearFocusField(focusManager: FocusManager) {
        focusManager.clearFocus()
    }

    fun hexToColor(hex: String?): Color {
        if (hex.isNullOrEmpty()) return Color.Transparent
        val cleanHex = hex.removePrefix("#")
        val colorLong = if (cleanHex.length == 8) {
            cleanHex.toLong(16)
        } else {
            cleanHex.toLong(16) or 0xFF000000
        }
        return Color(colorLong.toInt())
    }
}

// TODO: Figure out how to properly change theme for a single screen only
class StatusBarTheme @Inject constructor() {
    var isDarkTheme by mutableStateOf(false)
        private set

    fun toggleTheme(value: Boolean? = null) {
        isDarkTheme = value ?: !isDarkTheme
    }
}

val LocalStatusBarTheme = staticCompositionLocalOf<StatusBarTheme> {
    error("No ThemeState provided")
}

fun Intent.isFromHistory(): Boolean =
    flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0