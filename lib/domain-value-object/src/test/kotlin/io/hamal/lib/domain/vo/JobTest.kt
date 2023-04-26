package io.hamal.lib.domain.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class JobTest {
    @Nested
    @DisplayName("JobId")
    inner class JobIdTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    JobId("0x1337C0DE"),
                    JobId("0x1337C0DE")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    JobId("0x1337C0DE"),
                    JobId("0xC0DEBABEC0DE")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    JobId("0x1337C0DE").hashCode(),
                    JobId("0x1337C0DE").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    JobId("0x1337C0DE").hashCode(),
                    JobId("0xC0DEBABEC0DE").hashCode()
                )
            }
        }
    }

    @Nested
    @DisplayName("JobDefinitionId")
    inner class JobDefinitionIdTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    JobDefinitionId("0x1337C0DE"),
                    JobDefinitionId("0x1337C0DE")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    JobDefinitionId("0x1337C0DE"),
                    JobDefinitionId("0xC0DEBABEC0DE")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    JobDefinitionId("0x1337C0DE").hashCode(),
                    JobDefinitionId("0x1337C0DE").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    JobDefinitionId("0x1337C0DE").hashCode(),
                    JobDefinitionId("0xC0DEBABEC0DE").hashCode()
                )
            }
        }
    }

    @Nested
    @DisplayName("JobReference")
    inner class JobReferenceTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    JobReference("some-ref"),
                    JobReference("some-ref")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    JobReference("some-ref"),
                    JobReference("another-ref")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    JobReference("some-ref").hashCode(),
                    JobReference("some-ref").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    JobReference("some-ref").hashCode(),
                    JobReference("another-ref").hashCode()
                )
            }
        }
    }
}