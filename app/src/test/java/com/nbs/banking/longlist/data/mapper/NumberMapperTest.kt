package com.nbs.banking.longlist.data.mapper

import com.nbs.banking.longlist.data.model.NumberResponse
import com.nbs.banking.longlist.data.model.TransactionResponse
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class NumberMapperTest {

    @Test
    fun `when converting NumberResponse toDomain then returns matching NumberItem`() {
        // Given
        val response = NumberResponse(number = 42)

        // When
        val domainModel = response.toDomain()

        // Then
        assertEquals(42, domainModel.number)
    }

    @Test
    fun `when converting TransactionResponse toDomain then returns matching Transaction`() {
        // Given
        val date = LocalDate(2024, 3, 15)
        val response = TransactionResponse(
            id = 1,
            date = date,
            title = "Payment #1",
            amountCents = 1500L
        )

        // When
        val domainModel = response.toDomain()

        // Then
        assertEquals(1, domainModel.id)
        assertEquals(date, domainModel.date)
        assertEquals("Payment #1", domainModel.title)
        assertEquals(1500L, domainModel.amountCents)
    }
}
