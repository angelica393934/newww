package com.nbs.banking.note.data.mapper

import com.nbs.banking.coredatabase.entitiy.SampleEntity
import com.nbs.banking.note.domain.model.SampleNote
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Clock

class SampleMapperTest {

    private val now = Clock.System.now()

    @Test
    fun `when converting SampleEntity toDomain then returns matching SampleNote`() {
        // Given
        val entity = SampleEntity(id = 1L, note = "Test Note", createdAt = now)

        // When
        val domainModel = entity.toDomain()

        // Then
        assertEquals(1L, domainModel.id)
        assertEquals("Test Note", domainModel.note)
        assertEquals(now, domainModel.createdAt)
    }

    @Test
    fun `when converting SampleNote toEntity then returns matching SampleEntity`() {
        // Given
        val domainModel = SampleNote(id = 1L, note = "Test Note", createdAt = now)

        // When
        val entity = domainModel.toEntity()

        // Then
        assertEquals(1L, entity.id)
        assertEquals("Test Note", entity.note)
        assertEquals(now, entity.createdAt)
    }
}
