package com.nbs.banking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.nbs.banking.core.encryption.EncryptedKey
import com.nbs.banking.core.encryption.Encryptor
import com.nbs.banking.core.encryption.encryptedKey
import com.nbs.banking.core.exceptions.DecryptDataException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.security.GeneralSecurityException

@OptIn(ExperimentalCoroutinesApi::class)
class SecuredPreferenceDataStoreTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var securedPreferenceDataStore: SecuredPreferenceDataStoreImpl
    private val encryptor: Encryptor = mockk()

    @Before
    fun setUp() {
        val file = tmpFolder.newFile("test_secure_preferences_${System.nanoTime()}.preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { file }
        )
        securedPreferenceDataStore = SecuredPreferenceDataStoreImpl(
            dataStore = dataStore,
            encryptor = encryptor,
            scope = CoroutineScope(testDispatcher + SupervisorJob())
        )
    }

    @After
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun `save and get encrypted value successfully`() = runTest(testDispatcher) {
        // Given
        val key: EncryptedKey<String> = encryptedKey("auth_token")
        val plainValue = "bearer_token_123"
        val encryptedValue = "encrypted_ciphertext"

        every { encryptor.encrypt(plainValue, key.serializer) } returns encryptedValue
        every { encryptor.decrypt(encryptedValue, key.serializer) } returns plainValue

        // When
        securedPreferenceDataStore.save(key, plainValue)

        // Then
        val result = securedPreferenceDataStore.get(key)
        assertEquals(plainValue, result)
        assertEquals(plainValue, securedPreferenceDataStore.getEager(key))
    }

    @Test(expected = DecryptDataException::class)
    fun `get throws DecryptDataException on decryption failure for anti-tamper security`() {
        testScope.runTest {
            // Given
            val key: EncryptedKey<String> = encryptedKey("corrupted_token")
            val encryptedValue = "bad_ciphertext"

            every { encryptor.encrypt(any<String>(), any()) } returns encryptedValue
            every { encryptor.decrypt(encryptedValue, key.serializer) } throws DecryptDataException(
                GeneralSecurityException("decryption failed")
            )

            // When
            securedPreferenceDataStore.save(key, "some_value")

            // Then
            securedPreferenceDataStore.get(key)
        }
    }

    @Test
    fun `observe secured preference value`() = runTest(testDispatcher) {
        // Given
        val key: EncryptedKey<String> = encryptedKey("observe_token")
        val plainValue = "token_abc"
        val encryptedValue = "enc_token_abc"

        every { encryptor.encrypt(plainValue, key.serializer) } returns encryptedValue
        every { encryptor.decrypt(encryptedValue, key.serializer) } returns plainValue

        // When / Then
        securedPreferenceDataStore.observeOrNull(key).test {
            assertNull(awaitItem())

            securedPreferenceDataStore.save(key, plainValue)
            assertEquals(plainValue, awaitItem())

            securedPreferenceDataStore.remove(key)
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remove multiple keys and clear preferences`() = runTest(testDispatcher) {
        // Given
        val key1: EncryptedKey<String> = encryptedKey("token_1")
        val key2: EncryptedKey<String> = encryptedKey("token_2")
        val key3: EncryptedKey<String> = encryptedKey("token_3")

        every {
            encryptor.encrypt(any<String>(), String.serializer())
        } answers {
            "encrypted_${firstArg<String>()}"
        }

        every {
            encryptor.decrypt(any<String>(), String.serializer())
        } answers {
            firstArg<String>().removePrefix("encrypted_")
        }

        // When
        securedPreferenceDataStore.save(key1, "value_1")
        securedPreferenceDataStore.save(key2, "value_2")
        securedPreferenceDataStore.save(key3, "value_3")

        // Then
        assertEquals("value_1", securedPreferenceDataStore.get(key1))
        assertEquals("value_2", securedPreferenceDataStore.get(key2))
        assertEquals("value_3", securedPreferenceDataStore.get(key3))

        // When
        securedPreferenceDataStore.remove(key1, key2)

        // Then
        assertNull(securedPreferenceDataStore.get(key1))
        assertNull(securedPreferenceDataStore.get(key2))
        assertEquals("value_3", securedPreferenceDataStore.get(key3))

        // When
        securedPreferenceDataStore.clear()

        // Then
        assertNull(securedPreferenceDataStore.get(key1))
        assertNull(securedPreferenceDataStore.get(key2))
        assertNull(securedPreferenceDataStore.get(key3))
    }
}
