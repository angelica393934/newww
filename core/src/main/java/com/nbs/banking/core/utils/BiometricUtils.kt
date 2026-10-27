package com.nbs.banking.core.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.activity.result.ActivityResultLauncher
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.ProviderException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import timber.log.Timber

class BiometricUtils(private val context: Context) {
    fun checkBiometricAvailability(): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)

        val strongResult = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val weakResult = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)

        val hasFingerprint = context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
        val hasFace = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)

        val isFingerprintEnrolled = hasFingerprint && strongResult == BiometricManager.BIOMETRIC_SUCCESS
        val isFingerprintNotEnrolled = hasFingerprint && strongResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        val isFaceEnrolled = hasFace && weakResult == BiometricManager.BIOMETRIC_SUCCESS
        val isFaceNotEnrolled = hasFace && weakResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        val noneEnrolled = weakResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED

        val availability = when {
            isFingerprintEnrolled -> BiometricAvailability.USE_FINGERPRINT
            isFaceEnrolled -> BiometricAvailability.USE_FACE
            isFingerprintNotEnrolled -> BiometricAvailability.USE_FINGERPRINT
            isFaceNotEnrolled -> BiometricAvailability.USE_FACE
            else -> BiometricAvailability.NOT_AVAILABLE
        }

        Timber.d(
            "checkBiometricAvailability(): ${availability.name} \n" +
                "hasFingerPrint $hasFingerprint \n" +
                "hasFace $hasFace \n" +
                "isFingerprintEnrolled $isFingerprintEnrolled \n" +
                "isFaceEnrolled $isFaceEnrolled \n" +
                "noneEnrolled $noneEnrolled",
        )

        return availability
    }

    fun navigateToEnroll(launcher: ActivityResultLauncher<Intent>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val enrollIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, getAuthenticatorsType())
                    }
                } else {
                    @Suppress("DEPRECATION")
                    Intent(Settings.ACTION_FINGERPRINT_ENROLL)
                }
            launcher.launch(enrollIntent)
        }
    }

    fun checkBiometricStatus(): BiometricStatus = when (BiometricManager.from(context).canAuthenticate(getAuthenticatorsType())) {
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            BiometricStatus.NOT_ENROLLED
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            BiometricStatus.NO_HARDWARE
        }

        BiometricManager.BIOMETRIC_SUCCESS -> {
            BiometricStatus.AVAILABLE
        }

        else -> {
            BiometricStatus.UNAVAILABLE
        }
    }

    fun createPromptInfo(
        title: String,
        subtitle: String? = null,
        description: String? = null,
        negativeButtonText: String,
    ): BiometricPrompt.PromptInfo {
        val prompt = BiometricPrompt.PromptInfo
            .Builder()
            .setTitle(title)
            .setAllowedAuthenticators(getAuthenticatorsType())
            .setNegativeButtonText(negativeButtonText)

        if (!subtitle.isNullOrEmpty()) {
            prompt.setSubtitle(subtitle)
        }
        if (!description.isNullOrEmpty()) {
            prompt.setDescription(description)
        }

        return prompt.build()
    }

    fun createBiometricPromptCallback(
        activity: FragmentActivity,
        executor: Executor,
        onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit,
        onFailed: () -> Unit,
        onError: (errorCode: Int, message: CharSequence) -> Unit,
    ): BiometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errorCode, errString)
            }
        },
    )

    private fun getAuthenticatorsType(): Int = when (checkBiometricAvailability()) {
        BiometricAvailability.USE_FINGERPRINT -> {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }

        BiometricAvailability.USE_FACE -> {
            determineFaceAuthStrengthLevel()
        }

        BiometricAvailability.NOT_AVAILABLE -> {
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        }
    }

    private fun determineFaceAuthStrengthLevel(): Int {
        val strongBiometricCheckResult = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val hasStrongBiometric = strongBiometricCheckResult == BiometricManager.BIOMETRIC_SUCCESS

        Timber.d(
            """
            strongBiometricCheckResult: $strongBiometricCheckResult
            hasStrongBiometric: $hasStrongBiometric
            """.trimIndent(),
        )

        return if (hasStrongBiometric) BiometricManager.Authenticators.BIOMETRIC_STRONG else BiometricManager.Authenticators.BIOMETRIC_WEAK
    }

    fun checkBiometricChanged(): Boolean {
        val biometricStatus = checkBiometricStatus()
        return when (biometricStatus) {
            BiometricStatus.AVAILABLE -> {
                try {
                    // Attempt to use the auth-per-use key
                    val cipher = getCipher()
                    val key = getSecretKey(false) ?: return true
                    cipher.init(Cipher.ENCRYPT_MODE, key)
                    // If no exception is thrown, biometrics haven't changed
                    false
                } catch (e: KeyPermanentlyInvalidatedException) {
                    Timber.e(e)
                    // Biometrics have changed
                    true
                } catch (e: InvalidKeyException) {
                    Timber.e(e)
                    true
                } catch (e: NullPointerException) {
                    Timber.e(e)
                    true
                }
            }

            else -> {
                true
            }
        }
    }

    fun getSecretKey(shouldCreateNew: Boolean): SecretKey? {
        if (checkBiometricStatus() != BiometricStatus.AVAILABLE) return null

        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        var result: SecretKey? = null

        fun loadSecretKey() {
            result = (keyStore.getEntry(BIOMETRIC_KEY, null) as? KeyStore.SecretKeyEntry)?.secretKey
        }

        fun generateKeyAndLoad(): SecretKey? {
            try {
                generate()
                loadSecretKey()
                return result
            } catch (e: InvalidAlgorithmParameterException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Invalid parameters during generateKeyAndLoad()")
            } catch (e: ProviderException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Provider exception during generateKeyAndLoad()")
            } catch (e: KeyStoreException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "KeyStore exception during generateKeyAndLoad()")
            } catch (e: UnrecoverableKeyException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Unrecoverable key during generateKeyAndLoad()")
            } catch (e: NoSuchAlgorithmException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "No such algorithm during generateKeyAndLoad()")
            } catch (e: IOException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "IO exception during generateKeyAndLoad()")
            } catch (e: CertificateException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Certificate exception during generateKeyAndLoad()")
            }
            return null
        }

        return try {
            if (!keyStore.containsAlias(BIOMETRIC_KEY) || shouldCreateNew) {
                generateKeyAndLoad()
            } else {
                loadSecretKey()
                result
            }
        } catch (e: ProviderException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Corrupted key, retrying after deletion")
            deleteBiometricKey()
            if (checkBiometricStatus() != BiometricStatus.AVAILABLE) return null

            try {
                generateKeyAndLoad()
            } catch (retryException: InvalidAlgorithmParameterException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: InvalidAlgorithmParameterException")
                null
            } catch (retryException: ProviderException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: ProviderException")
                null
            } catch (retryException: KeyStoreException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: KeyStoreException")
                null
            } catch (retryException: UnrecoverableKeyException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: UnrecoverableKeyException")
                null
            } catch (retryException: NoSuchAlgorithmException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: NoSuchAlgorithmException")
                null
            } catch (retryException: IOException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: IOException")
                null
            } catch (retryException: CertificateException) {
                Timber.tag(TAG_BIOMETRIC_UTILS).e(retryException, "Retry failed: CertificateException")
                null
            }
        } catch (e: KeyPermanentlyInvalidatedException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).d(e, "Key permanently invalidated")
            null
        } catch (e: KeyStoreException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).d(e, "KeyStoreException in getSecretKey()")
            null
        } catch (e: UnrecoverableKeyException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).d(e, "UnrecoverableKeyException in getSecretKey()")
            null
        }
    }

    fun generate(): SecretKey? {
        try {
            if (checkBiometricStatus() != BiometricStatus.AVAILABLE) {
                Timber.tag(TAG_BIOMETRIC_UTILS).d("Biometric not available, skipping key generation")
                return null
            }

            KeyStore.getInstance(KEYSTORE_PROVIDER).run {
                load(null, null)
                return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).run {
                    init(
                        KeyGenParameterSpec.Builder(
                            BIOMETRIC_KEY,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        )
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setInvalidatedByBiometricEnrollment(true)
                            .setUserAuthenticationRequired(
                                getAuthenticatorsType() == BiometricManager.Authenticators.BIOMETRIC_STRONG,
                            )
                            .setKeySize(BIOMETRIC_KEY_SIZE)
                            .build(),
                    )
                    generateKey()
                }
            }
        } catch (e: InvalidAlgorithmParameterException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Invalid parameters during generate()")
        } catch (e: ProviderException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "ProviderException in generate(), retrying after deletion")
            deleteBiometricKey()
            return retryGenerateAfterDelete()
        } catch (e: KeyStoreException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "KeyStoreException in generate()")
        } catch (e: NoSuchAlgorithmException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "NoSuchAlgorithmException in generate()")
        } catch (e: NoSuchProviderException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "NoSuchProviderException in generate()")
        } catch (e: IOException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "IOException in generate()")
        } catch (e: CertificateException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "CertificateException in generate()")
        }

        return null
    }

    private fun retryGenerateAfterDelete(): SecretKey? {
        try {
            KeyStore.getInstance(KEYSTORE_PROVIDER).run {
                load(null, null)
                return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER).run {
                    init(
                        KeyGenParameterSpec.Builder(
                            BIOMETRIC_KEY,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        )
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setInvalidatedByBiometricEnrollment(true)
                            .setUserAuthenticationRequired(
                                getAuthenticatorsType() == BiometricManager.Authenticators.BIOMETRIC_STRONG,
                            )
                            .setKeySize(BIOMETRIC_KEY_SIZE)
                            .build(),
                    )
                    generateKey()
                }
            }
        } catch (e: InvalidAlgorithmParameterException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: InvalidAlgorithmParameterException")
        } catch (e: ProviderException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: ProviderException")
        } catch (e: KeyStoreException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: KeyStoreException")
        } catch (e: NoSuchAlgorithmException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: NoSuchAlgorithmException")
        } catch (e: NoSuchProviderException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: NoSuchProviderException")
        } catch (e: IOException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: IOException")
        } catch (e: CertificateException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "Retry failed: CertificateException")
        }

        return null
    }

    private fun deleteBiometricKey() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
                load(null)
            }
            if (keyStore.containsAlias(BIOMETRIC_KEY)) {
                keyStore.deleteEntry(BIOMETRIC_KEY)
                Timber.tag(TAG_BIOMETRIC_UTILS).d("Deleted key alias: $BIOMETRIC_KEY")
            }
        } catch (e: KeyStoreException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "KeyStoreException during delete")
        } catch (e: NoSuchAlgorithmException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "NoSuchAlgorithmException during delete")
        } catch (e: IOException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "IOException during delete")
        } catch (e: CertificateException) {
            Timber.tag(TAG_BIOMETRIC_UTILS).e(e, "CertificateException during delete")
        }
    }

    private fun getCipher(): Cipher = Cipher.getInstance(
        KeyProperties.KEY_ALGORITHM_AES +
            "/" +
            KeyProperties.BLOCK_MODE_GCM +
            "/" +
            KeyProperties.ENCRYPTION_PADDING_NONE,
    )

    companion object {
        private const val BIOMETRIC_KEY = "biometric_key"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val BIOMETRIC_KEY_SIZE = 256
        private const val TAG_BIOMETRIC_UTILS = "Biometric Utils"
    }
}

enum class BiometricAvailability {
    USE_FINGERPRINT,
    USE_FACE,
    NOT_AVAILABLE,
    ;

    fun mapToBiometricType(): BiometricType = when (this) {
        USE_FINGERPRINT -> BiometricType.FINGERPRINT
        USE_FACE -> BiometricType.FACE
        NOT_AVAILABLE -> BiometricType.FINGERPRINT
    }
}

enum class BiometricStatus {
    AVAILABLE,
    UNAVAILABLE,
    NOT_ENROLLED,
    NO_HARDWARE,
}

enum class BiometricType(val id: String) {
    UNDEFINED(""),
    FINGERPRINT("touch"),
    FACE("face"),
}