package com.nbs.banking.core.encryption

import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.DIGEST_SHA256
import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val AES_CBC_CIPHER_ENCRYPTION_TYPE = "AES/CBC/PKCS5Padding"

class AesEncryptionManager @Inject constructor() : EncryptionService {
    override fun encrypt(
        input: String,
        keyData: String,
        ivData: String,
    ): String {
        val secretKey = generateSecretKey(keyData)
        val ivSpec = generateIvSpec(ivData)

        val cipher = Cipher.getInstance(AES_CBC_CIPHER_ENCRYPTION_TYPE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)

        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    override fun decrypt(
        encryptedData: String,
        keyData: String,
        ivData: String,
    ): String {
        val secretKey = generateSecretKey(keyData)
        val ivSpec = generateIvSpec(ivData)

        val cipher = Cipher.getInstance(AES_CBC_CIPHER_ENCRYPTION_TYPE)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        val decodedBytes = Base64.decode(encryptedData, Base64.NO_WRAP)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }

    private fun generateSecretKey(keyData: String): SecretKey {
        val hashedKey = sha256(keyData).substring(FIRST_INDEX_OF_STRING, SECRET_KEY_LENGTH)
        return SecretKeySpec(hashedKey.toByteArray(), KeyProperties.KEY_ALGORITHM_AES)
    }

    private fun generateIvSpec(ivData: String): IvParameterSpec {
        val hashedIv = sha256(ivData).substring(FIRST_INDEX_OF_STRING, IV_KEY_LENGTH)
        return IvParameterSpec(hashedIv.toByteArray())
    }

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance(DIGEST_SHA256)
        digest.update(input.toByteArray())
        val hashedBytes = digest.digest()
        return hashedBytes.joinToString("") { String.format(BYTE_TO_HEX, it) }
    }

    companion object {
        private const val SECRET_KEY_LENGTH = 32
        private const val IV_KEY_LENGTH = 16
        private const val BYTE_TO_HEX = "%02x"
        private const val FIRST_INDEX_OF_STRING = 0
    }
}
