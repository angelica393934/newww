package com.nbs.banking.corenetwork.helper.ssl

interface DynamicSSLProvider {
    suspend fun ensureInitialized()

    fun getFingerprintsSync(url: String): List<String>
}
