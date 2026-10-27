package com.nbs.banking.coredatabase.converter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.time.Clock

class InstantConverterTest {

    @Test
    fun `when converting epochMillis to Instant then returns correct Instant`() {
        // Given
        val now = Clock.System.now()
        val millis = now.toEpochMilliseconds()

        // When
        val result = InstantConverter.fromEpochMilliseconds(millis)

        // Then
        assertEquals(millis, result?.toEpochMilliseconds())
    }

    @Test
    fun `when converting null epochMillis then returns null`() {
        // When
        val result = InstantConverter.fromEpochMilliseconds(null)

        // Then
        assertNull(result)
    }

    @Test
    fun `when converting Instant to epochMillis then returns correct millis`() {
        // Given
        val now = Clock.System.now()

        // When
        val millis = InstantConverter.toEpochMilliseconds(now)

        // Then
        assertEquals(now.toEpochMilliseconds(), millis)
    }

    @Test
    fun `when converting null Instant then returns null`() {
        // When
        val result = InstantConverter.toEpochMilliseconds(null)

        // Then
        assertNull(result)
    }
}
