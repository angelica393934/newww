package com.nbs.banking.core.utils

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.FileProvider
import com.nbs.banking.core.extensions.emptyString
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import androidx.core.net.toUri

object FileStorageManager {
    /**
     * Download file from URL
     *
     * @param context Context
     * @param coroutineContext CoroutineContext
     * @param url String
     * @param destinationFileName String
     * @return Unit
     *
     */
    suspend fun downloadFile(
        context: Context,
        coroutineContext: CoroutineContext = Dispatchers.IO,
        url: String,
        destinationFileName: String,
        usePrefix: Boolean = true,
    ) {
        withContext(coroutineContext) {
            try {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadUri = url.toUri()
                val request = DownloadManager.Request(downloadUri)

                // Android 10 version (API level 29) and above need to use Storage Schema
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val downloadDirectory = context.getDownloadsDirectory()
                    request.apply {
                        setTitle(destinationFileName)
                        setDescription("Downloading $destinationFileName...")
                        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                        setAllowedOverRoaming(false)
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationUri(
                            Uri.fromFile(
                                File(
                                    downloadDirectory.absolutePath,
                                    (if (usePrefix) "${context.getAppName()}_${System.currentTimeMillis()}_" else emptyString()) + destinationFileName,
                                ),
                            ),
                        )
                    }
                    downloadManager.enqueue(request)
                } else {
                    request.apply {
                        setTitle(destinationFileName)
                        setDescription("Downloading $destinationFileName...")
                        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                        setAllowedOverRoaming(false)
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                        setDestinationInExternalPublicDir(
                            "/${Environment.DIRECTORY_DOWNLOADS}/${context.getAppName()}/",
                            (if (usePrefix) "${context.getAppName()}_${System.currentTimeMillis()}_" else emptyString()) + destinationFileName,
                        )
                    }
                    downloadManager.enqueue(request)
                }
            } catch (e: NullPointerException) {
                Timber.e(e)
                throw e
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
                throw e
            }
        }
    }

    fun downloadBase64FileAndOpen(
        context: Context,
        fileContent: String,
        fileName: String,
        mime: String = "application/pdf",
        onError: (Throwable) -> Unit = {},
    ) {
        try {
            val pdfBytes = Base64.decode(fileContent, Base64.NO_WRAP)
            val fileUri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mime)
                    put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/${context.getAppName()}")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                fileUri = resolver.insert(collection, contentValues) ?: throw IOException()

                resolver.openOutputStream(fileUri).use { output ->
                    output?.write(pdfBytes)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(fileUri, contentValues, null, null)
            } else {
                val parentDir = context.getDownloadsDirectory()
                val file = File(parentDir, fileName)

                FileOutputStream(file).use { fos ->
                    fos.write(pdfBytes)
                }
                fileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(intent)
        } catch (e: IOException) {
            Timber.e(e)
            onError(e)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
            onError(e)
        } catch (e: SecurityException) {
            Timber.e(e)
            onError(e)
        } catch (e: NullPointerException) {
            Timber.e(e)
            onError(e)
        } catch (e: ActivityNotFoundException) {
            Timber.e(e)
            onError(e)
        }
    }
}

/**
 * Get application name
 *
 * @return String
 *
 */
fun Context.getAppName(): String {
    val packageManager = packageManager
    val applicationInfo: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        Timber.e(e)
        null
    }
    return if (applicationInfo != null) {
        packageManager.getApplicationLabel(applicationInfo).toString()
    } else {
        "Unknown Application"
    }
}

fun Context.getPublicDownloadDirectory(): File {
    val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val dproDownloadDir = File(downloadDir, getAppName())
    if (!dproDownloadDir.exists()) dproDownloadDir.mkdir()

    return dproDownloadDir
}

/**
 * Get storage directory used for Android 10 (API level 29) and above
 *
 * @return File
 *
 * @author Fachridan Tio Mu'afa
 */
fun Context.getDownloadsDirectory(): File = if (Environment.getExternalStorageState() == null) {
    val file = File(
        Environment.getDataDirectory().absolutePath +
            "/${Environment.DIRECTORY_DOWNLOADS}/${getAppName()}/",
    )
    if (!file.exists()) file.mkdirs()
    file
} else {
    val file = File(
        Environment.getExternalStorageDirectory().absolutePath +
            "/${Environment.DIRECTORY_DOWNLOADS}/${getAppName()}/",
    )
    if (!file.exists()) file.mkdirs()
    file
}

/**
 * Get storage directory used for Android 10 (API level 29) and above
 *
 * @return File
 *
 * @author Fachridan Tio Mu'afa
 */
fun Context.getPicturesDirectory(): File = if (Environment.getExternalStorageState() == null) {
    val file = File(
        Environment.getDataDirectory().absolutePath +
            "/${Environment.DIRECTORY_PICTURES}/${getAppName()}/",
    )
    if (!file.exists()) file.mkdirs()
    file
} else {
    val file = File(
        Environment.getExternalStorageDirectory().absolutePath +
            "/${Environment.DIRECTORY_PICTURES}/${getAppName()}/",
    )
    if (!file.exists()) file.mkdirs()
    file
}
