package com.nbs.banking.corenetwork.helper.ssl

import com.nbs.banking.core.storage.DynamicSSLStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

@Singleton
class DynamicSSLProviderImpl @Inject constructor(
    private val dynamicSSLStorage: DynamicSSLStorage,
    private val json: Json,
) : DynamicSSLProvider {
    // ----------------------------
    // Cached fingerprints mapping (domain → fingerprints)
    // Volatile ensures all threads see the most up-to-date reference
    // ----------------------------
    @Volatile
    private var cachedFingerprints: Map<String, List<String>?>? = null
    private val cacheLock = Mutex()
    private val initialized = CompletableDeferred<Unit>()

    override suspend fun ensureInitialized() {
        // Fast path: already initialized
        if (initialized.isCompleted) return

        // Slow path: only one coroutine allowed to run this block
        cacheLock.withLock {
            // Recheck condition after acquiring lock to avoid race
//            if (!initialized.isCompleted) {
//                val data = getSSLSource()
//                cachedFingerprints =
//                    data.pairs.associateTo(mutableMapOf()) { it.domain to it.fingerprints }
//                // Mark initialization complete; wakes up all awaiting coroutines
//                initialized.complete(Unit)
//            }
        }
    }

    // ----------------------------
    // Safe synchronous access for clients (e.g., OkHttp interceptors)
    // Blocks thread only until initialization finishes
    // ----------------------------
    override fun getFingerprintsSync(url: String): List<String> {
        // Wait only if initialization hasn't completed yet
        if (!initialized.isCompleted) {
            runBlocking { initialized.await() }
        }
        // Safe read from the volatile cache
        return cachedFingerprints?.get(url).orEmpty()
    }

    // ----------------------------
    // Internal helper to decide which SSL source to use
    // Priority: Embedded → Local → Remote (based on version timestamps)
    // ----------------------------
//    private suspend fun getSSLSource(): DynamicSSL {
//        val embeddedConfig = getEmbeddedConfig()
//        val localFingerprint = dynamicSSLStorage.load()
//        val remoteConfigSSL =
//            DbpConfig.getObject(ConfigKey.DYNAMIC_SSL_PINNING, DynamicSSL::class.java)
//
//        return when {
//            localFingerprint.isNull() -> {
//                dynamicSSLStorage.save(embeddedConfig)
//                embeddedConfig
//            }
//
//            !isDateNewerOrSame(
//                embeddedConfig.lastUpdatedAt,
//                localFingerprint!!.lastUpdatedAt,
//                YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_DATE_TIME_PATTERN,
//            ) -> {
//                dynamicSSLStorage.save(embeddedConfig)
//                embeddedConfig
//            }
//
//            remoteConfigSSL.isNull() -> {
//                localFingerprint
//            }
//
//            isDateNewer(
//                localFingerprint.lastUpdatedAt,
//                remoteConfigSSL!!.lastUpdatedAt,
//                YEAR_MONTH_DATE_HOUR_MINUTE_SECOND_DATE_TIME_PATTERN,
//            ) -> {
//                dynamicSSLStorage.save(remoteConfigSSL)
//                remoteConfigSSL
//            }
//
//            else -> {
//                localFingerprint
//            }
//        }
//    }

    private fun isDateNewer(
        date1: String,
        date2: String,
        pattern: String,
    ): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern(pattern)
        val formattedDate1 = LocalDateTime.parse(date1, dateFormatter)
        val formattedDate2 = LocalDateTime.parse(date2, dateFormatter)
        return formattedDate2.isAfter(formattedDate1)
    }

    private fun isDateNewerOrSame(
        date1: String,
        date2: String,
        pattern: String,
    ): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern(pattern)
        val formattedDate1 = LocalDateTime.parse(date1, dateFormatter)
        val formattedDate2 = LocalDateTime.parse(date2, dateFormatter)
        return !formattedDate1.isAfter(formattedDate2)
    }

//    private fun getEmbeddedConfig(): DynamicSSL {
//        val jsonString = getEmbeddedConfigNative()
//        return json.decodeFromString<DynamicSSL>(jsonString)
//    }

    private external fun getEmbeddedConfigNative(): String
}