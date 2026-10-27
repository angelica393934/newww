package com.nbs.banking.randomactivityidea.data.mapper

import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RandomActivityIdeaMapperTest {

    @Test
    fun `when converting RandomActivityIdeaResponse toDomain with non-null values then returns matching RandomActivityIdea`() {
        val response = RandomActivityIdeaResponse(
            activity = "Read a book",
            availability = 0.9,
            type = "education",
            participants = 1,
            price = 0.0,
            accessibility = "Easy",
            duration = "1 hour",
            kidFriendly = true,
            link = "https://books.com",
            key = "987654",
        )

        val domain = response.toDomain()

        assertEquals("Read a book", domain.activity)
        assertEquals(0.9, domain.availability, 0.001)
        assertEquals("education", domain.type)
        assertEquals(1, domain.participants)
        assertEquals(0.0, domain.price, 0.001)
        assertEquals("Easy", domain.accessibility)
        assertEquals("1 hour", domain.duration)
        assertEquals(true, domain.kidFriendly)
        assertEquals("https://books.com", domain.link)
        assertEquals("987654", domain.key)
    }

    @Test
    fun `when converting RandomActivityIdeaResponse toDomain with null values then uses default values`() {
        val response = RandomActivityIdeaResponse(
            activity = null,
            availability = null,
            type = null,
            participants = null,
            price = null,
            accessibility = null,
            duration = null,
            kidFriendly = null,
            link = null,
            key = null,
        )

        val domain = response.toDomain()

        assertEquals("", domain.activity)
        assertEquals(0.0, domain.availability, 0.001)
        assertEquals("", domain.type)
        assertEquals(0, domain.participants)
        assertEquals(0.0, domain.price, 0.001)
        assertEquals("", domain.accessibility)
        assertEquals("", domain.duration)
        assertFalse(domain.kidFriendly)
        assertEquals("", domain.link)
        assertEquals("", domain.key)
    }
}
