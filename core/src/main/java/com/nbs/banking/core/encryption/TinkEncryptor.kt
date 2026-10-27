package com.nbs.banking.core.encryption

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.nbs.banking.core.BuildConfig
import com.nbs.banking.core.exceptions.DecryptDataException
import com.nbs.banking.core.exceptions.EncryptDataException
import com.nbs.banking.core.exceptions.EncryptionException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.security.GeneralSecurityException
import javax.inject.Inject
import kotlin.getValue

class TinkEncryptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : Encryptor, EncryptionKeyManager {
    private val keysetManager: AndroidKeysetManager by lazy {
        AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(KEY_TEMPLATE)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
    }

    private fun getAead(): Aead =
        keysetManager.keysetHandle.getPrimitive(
            RegistryConfiguration.get(),
            Aead::class.java
        )

    override fun <T> encrypt(
        value: T,
        serializer: KSerializer<T>
    ): String {
        try {
            val plainText = json.encodeToString(serializer, value).toByteArray()
            val encrypted = getAead().encrypt(plainText, ASSOCIATED_DATA)
            return Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: SerializationException) {
            throw EncryptDataException(e, e.message)
        } catch (e: GeneralSecurityException) {
            throw EncryptDataException(e, e.message)
        }
    }

    override fun <T> decrypt(
        value: String,
        serializer: KSerializer<T>
    ): T {
        try {
            val decoded = Base64.decode(value, Base64.NO_WRAP)
            val decrypted = getAead().decrypt(decoded, ASSOCIATED_DATA)
            return json.decodeFromString(serializer, String(decrypted, Charsets.UTF_8))
        } catch (e: SerializationException) {
            throw DecryptDataException(e, e.message)
        } catch (e: GeneralSecurityException) {
            throw DecryptDataException(e, e.message)
        } catch (e: IllegalArgumentException) {
            throw DecryptDataException(e, e.message)
        }
    }

    override fun logDebugInfo(){
        if (BuildConfig.DEBUG && BuildConfig.BUILD_TYPE == "debug") {
            Timber.tag("AEAD")
                .d("currentKeyId: ${keysetManager.keysetHandle.primary.key.idRequirementOrNull}")

            val handle = keysetManager.keysetHandle

            (0 until handle.size()).forEach { index ->
                val key = handle.getAt(index)
                Timber.tag("AEAD").d(
                    "id=${key.id}, primary=${key.isPrimary}, " +
                            "status=${key.status}, parameters=${key.key.parameters}"
                )
            }
        }
    }

    override fun rotateKey() {
        try {
            val handleBefore = keysetManager.keysetHandle

            val oldIds = (0 until handleBefore.size())
                .map { handleBefore.getAt(it).id }
                .toSet()

            keysetManager.add(KEY_TEMPLATE)

            val handleAfter = keysetManager.keysetHandle

            val newKeyId = (0 until handleAfter.size())
                .map { handleAfter.getAt(it).id }
                .toSet()
                .subtract(oldIds)
                .firstOrNull() ?: throw GeneralSecurityException(
                "Key rotation failed: new key was not added."
            )

            keysetManager.setPrimary(newKeyId)
        } catch (e: GeneralSecurityException) {
            throw EncryptionException(e, e.message)
        }
    }

    //TODO: CHANGE THE VALUE TO SOMETHING ELSE
    companion object {
        private const val KEYSET_NAME = "nbs_android_secure_keyset"
        private const val PREFERENCE_FILE = "nbs_android_secure_prefs"
        private const val MASTER_KEY_ALIAS = "nbs_android_secure_master_key"
        private const val MASTER_KEY_URI = "android-keystore://$MASTER_KEY_ALIAS"
        private val ASSOCIATED_DATA = "nbs_secure_datastore".toByteArray(Charsets.UTF_8)
        private val KEY_TEMPLATE = KeyTemplate.createFrom(PredefinedAeadParameters.AES256_GCM)
    }
}