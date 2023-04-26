package io.hamal.lib.domain.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class TriggerTest {

    @Nested
    @DisplayName("TriggerId")
    inner class TriggerIdTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    TriggerId("0x1337C0DE"),
                    TriggerId("0x1337C0DE")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    TriggerId("0x1337C0DE"),
                    TriggerId("0xC0DEBABEC0DE")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    TriggerId("0x1337C0DE").hashCode(),
                    TriggerId("0x1337C0DE").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    TriggerId("0x1337C0DE").hashCode(),
                    TriggerId("0xC0DEBABEC0DE").hashCode()
                )
            }
        }
    }
}