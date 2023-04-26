package io.hamal.lib.domain.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class FlowTest {

    @Nested
    @DisplayName("FlowId")
    inner class FlowIdTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    FlowId("0xbadc0de"),
                    FlowId("0xbadc0de")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    FlowId("0xbadc0de"),
                    FlowId("0x1337C0DE")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    FlowId("0xbadc0de").hashCode(),
                    FlowId("0xbadc0de").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    FlowId("0xbadc0de").hashCode(),
                    FlowId("0x1337C0DE").hashCode()
                )
            }
        }
    }
}