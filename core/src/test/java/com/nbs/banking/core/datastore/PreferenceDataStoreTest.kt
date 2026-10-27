package com.nbs.banking.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import com.nbs.banking.core.exceptions.DataStoreSerializationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertFailsWith

@Serializable
data class TestUser(val name: String, val age: Int)

@OptIn(ExperimentalCoroutinesApi::class)
class PreferenceDataStoreTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferenceDataStore: PreferenceDataStoreImpl
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        encodeDefaults = false
        useAlternativeNames = false
        prettyPrint = true
    }

    @Before
    fun setUp() {
        val file = tmpFolder.newFile("test_preferences_${System.nanoTime()}.preferences_pb")
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { file }
        )
        preferenceDataStore = PreferenceDataStoreImpl(
            dataStore = dataStore,
            json = json,
            scope = CoroutineScope(testDispatcher + SupervisorJob())
        )
    }

    @After
    fun tearDown() {
        testScope.cancel()
    }

    @Test
    fun `save and get primitive value with defaults`() = runTest(testDispatcher) {
        // Given
        val key = stringPreferencesKey("test_key_${System.nanoTime()}")
        val value = "test_value"
        val defaultValue = "default_value"

        // When / Then (Before Saving)
        assertEquals(defaultValue, preferenceDataStore.getEager(key, defaultValue))
        assertEquals(defaultValue, preferenceDataStore.get(key, defaultValue))
        assertNull(preferenceDataStore.getEager(key))
        assertNull(preferenceDataStore.get(key))

        // When
        preferenceDataStore.save(key, value)

        // Then (After Saving)
        assertEquals(value, preferenceDataStore.get(key))
        assertEquals(value, preferenceDataStore.getEager(key))
        assertEquals(value, preferenceDataStore.get(key, defaultValue))
        assertEquals(value, preferenceDataStore.getEager(key, defaultValue))
    }

    @Test
    fun `save and get object serialization`() = runTest(testDispatcher) {
        // Given
        val key = objectPreferencesKey("user_key_${System.nanoTime()}", TestUser.serializer())
        val user = TestUser("Bob", 25)
        val defaultUser = TestUser("Default", 0)

        // When / Then (Before Saving)
        assertEquals(
            defaultUser,
            preferenceDataStore.getObjectEager(key, defaultUser)
        )
        assertEquals(
            defaultUser,
            preferenceDataStore.getObject(key, defaultUser)
        )
        assertNull(preferenceDataStore.getObjectEager(key))
        assertNull(preferenceDataStore.getObject(key))

        // When
        preferenceDataStore.saveObject(key, user)

        // Then
        assertEquals(user, preferenceDataStore.getObjectEager(key))
        assertEquals(user, preferenceDataStore.getObject(key))
    }

    @Test
    fun `observe preference value and default`() = runTest(testDispatcher) {
        // Given
        val key = stringPreferencesKey("observe_key_${System.nanoTime()}")

        // When / Then
        preferenceDataStore.observeOrNull(key).test {
            assertNull(awaitItem())

            preferenceDataStore.save(key, "hello")
            assertEquals("hello", awaitItem())

            preferenceDataStore.remove(key)
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Given
        val keyDefault = stringPreferencesKey("observe_default_key_${System.nanoTime()}")

        // When / Then
        preferenceDataStore.observe(keyDefault, "default").test {
            assertEquals("default", awaitItem())

            preferenceDataStore.save(keyDefault, "active")
            assertEquals("active", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observe object serialization`() = runTest(testDispatcher) {
        // Given
        val keyNull =
            objectPreferencesKey("observe_user_null_${System.nanoTime()}", TestUser.serializer())
        val user = TestUser("Charlie", 40)

        // When / Then
        preferenceDataStore.observeObjectOrNull(keyNull).test {
            assertNull(awaitItem())

            preferenceDataStore.saveObject(keyNull, user)
            assertEquals(user, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Given
        val keyDefault =
            objectPreferencesKey("observe_user_default_${System.nanoTime()}", TestUser.serializer())

        // When / Then
        preferenceDataStore.observeObject(keyDefault, TestUser("Guest", 18))
            .test {
                assertEquals(TestUser("Guest", 18), awaitItem())

                preferenceDataStore.saveObject(keyDefault, user)
                assertEquals(user, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `remove multiple keys and clear preferences`() = runTest(testDispatcher) {
        // Given
        val key1 = stringPreferencesKey("key1_${System.nanoTime()}")
        val key2 = stringPreferencesKey("key2_${System.nanoTime()}")

        preferenceDataStore.save(key1, "val1")
        preferenceDataStore.save(key2, "val2")

        assertEquals("val1", preferenceDataStore.get(key1))
        assertEquals("val2", preferenceDataStore.get(key2))

        // When
        preferenceDataStore.remove(key1)

        // Then
        assertNull(preferenceDataStore.get(key1))
        assertEquals("val2", preferenceDataStore.get(key2))

        // When
        preferenceDataStore.clear()

        // Then
        assertNull(preferenceDataStore.get(key2))
    }

    @Test
    fun `getObject throws when stored json is invalid`() = runTest(testDispatcher) {
        // Given
        val key = objectPreferencesKey(
            "invalid_json_${System.nanoTime()}", TestUser.serializer()
        )

        dataStore.edit {
            it[key.key] = """{"name": "Bob", "age":"""
        }

        // When / Then
        assertFailsWith<DataStoreSerializationException> {
            preferenceDataStore.getObject(key)
        }
    }

    @Test
    fun `getObjectEager throws when stored json is invalid`() = runTest(testDispatcher) {
        // Given
        val key = objectPreferencesKey(
            "invalid_json_eager_${System.nanoTime()}", TestUser.serializer()
        )

        dataStore.edit {
            it[key.key] = """{"name": "Bob", "age":"""
        }

        // When / Then
        assertFailsWith<DataStoreSerializationException> {
            preferenceDataStore.getObjectEager(
                key,
            )
        }
    }
}
