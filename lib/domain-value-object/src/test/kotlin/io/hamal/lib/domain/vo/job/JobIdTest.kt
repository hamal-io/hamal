package io.hamal.lib.domain.vo.job

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class JobIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                JobId("410908f7-7e1c-4556-81d9-a29a5e5f42a3"),
                JobId("410908f7-7e1c-4556-81d9-a29a5e5f42a3")
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                JobId("410908f7-7e1c-4556-81d9-a29a5e5f42a3"),
                JobId("f19badf6-07eb-4d4a-a637-393ed66965db")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                JobId("410908f7-7e1c-4556-81d9-a29a5e5f42a3").hashCode(),
                JobId("410908f7-7e1c-4556-81d9-a29a5e5f42a3").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                JobId("410908f7-7e1c-4556-81d9-a29a5e5f42a3").hashCode(),
                JobId("f19badf6-07eb-4d4a-a637-393ed66965db").hashCode()
            )
        }
    }
}