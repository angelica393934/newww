package com.nbs.banking.core.encryption

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.nbs.banking.core.exceptions.DecryptDataException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotEquals

@Serializable
private data class TestPayload(
    val id: String,
    val amount: Double
)

class TinkEncryptorTest {

    private val context: Context = mockk()
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = false
        useAlternativeNames = false
        prettyPrint = true
    }

    private lateinit var keysetHandle: KeysetHandle
    private lateinit var tinkEncryptor: TinkEncryptor

    @Before
    fun setUp() {
        AeadConfig.register()

        mockkStatic(Base64::class)
        every {
            Base64.encodeToString(any<ByteArray>(), any())
        } answers {
            java.util.Base64.getEncoder()
                .encodeToString(firstArg<ByteArray>())
        }

        every {
            Base64.decode(any<String>(), any())
        } answers {
            java.util.Base64.getDecoder()
                .decode(firstArg<String>())
        }

        keysetHandle = KeysetHandle.generateNew(PredefinedAeadParameters.AES256_GCM)
        val mockBuilder = mockk<AndroidKeysetManager.Builder>()
        val mockKeysetManager = mockk<AndroidKeysetManager>()
        mockkConstructor(AndroidKeysetManager.Builder::class)

        every {
            anyConstructed<AndroidKeysetManager.Builder>()
                .withSharedPref(
                    any<Context>(),
                    any<String>(),
                    any<String>()
                )
        } returns mockBuilder

        every { mockBuilder.withKeyTemplate(any<KeyTemplate>()) } returns mockBuilder
        every { mockBuilder.withMasterKeyUri(any<String>()) } returns mockBuilder
        every { mockBuilder.build() } returns mockKeysetManager
        every { mockKeysetManager.keysetHandle } returns keysetHandle

        tinkEncryptor = TinkEncryptor(
            context = context,
            json = json
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `encrypt actually encrypts value`() {
        // Given
        val payload = TestPayload(
            id = "tx_1001",
            amount = 150.75
        )

        val plainText = json.encodeToString(
            TestPayload.serializer(),
            payload
        )

        // When
        val encrypted = tinkEncryptor.encrypt(
            payload,
            TestPayload.serializer()
        )

        // Then
        assertNotEquals(plainText, encrypted)
    }

    @Test
    fun `decrypt actually decrypts encrypted value`() {
        // Given
        val payload = TestPayload(
            id = "tx_1001",
            amount = 150.75
        )

        val encrypted = tinkEncryptor.encrypt(
            payload,
            TestPayload.serializer()
        )

        // When
        val decrypted = tinkEncryptor.decrypt(
            encrypted,
            TestPayload.serializer()
        )

        // Then
        assertEquals(payload, decrypted)
    }

    @Test
    fun `encrypting same value produces different ciphertext`() {
        // Given
        val value = "secret"

        // When
        val encrypted1 = tinkEncryptor.encrypt(
            value,
            String.serializer()
        )

        val encrypted2 = tinkEncryptor.encrypt(
            value,
            String.serializer()
        )

        // Then
        assertNotEquals(encrypted1, encrypted2)
    }

    @Test
    fun `decrypt throws DecryptDataException when ciphertext is corrupted`() {
        // Given
        val encrypted = tinkEncryptor.encrypt(
            "secret",
            String.serializer()
        )

        val corrupted = encrypted.dropLast(1) +
                if (encrypted.last() == 'A') 'B' else 'A'

        // When / Then
        assertThrows(DecryptDataException::class.java) {
            tinkEncryptor.decrypt(
                corrupted,
                String.serializer()
            )
        }
    }

    @Test
    fun `decrypt throws DecryptDataException when base64 is invalid`() {
        // Given
        val invalidData = "invalid_encrypted_data_123!!!"

        // When / Then
        assertThrows(DecryptDataException::class.java) {
            tinkEncryptor.decrypt(
                invalidData,
                String.serializer()
            )
        }
    }
}
