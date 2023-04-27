package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.helper.SerializationTestHelper.generateTestCases
import io.hamal.lib.util.Snowflake
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

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
                    JobId(Snowflake.Id(23)),
                    JobId(Snowflake.Id(23))
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    JobId(Snowflake.Id(23)),
                    JobId(Snowflake.Id(127))
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    JobId(Snowflake.Id(23)).hashCode(),
                    JobId(Snowflake.Id(23)).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    JobId(Snowflake.Id(23)).hashCode(),
                    JobId(Snowflake.Id(127)).hashCode()
                )
            }
        }

        @TestFactory
        fun Serialization() = generateTestCases(JobId(Snowflake.Id(23)), "23")
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
                    JobDefinitionId(Snowflake.Id(23)),
                    JobDefinitionId(Snowflake.Id(23))
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    JobDefinitionId(Snowflake.Id(23)),
                    JobDefinitionId(Snowflake.Id(127))
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    JobDefinitionId(Snowflake.Id(23)).hashCode(),
                    JobDefinitionId(Snowflake.Id(23)).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    JobDefinitionId(Snowflake.Id(23)).hashCode(),
                    JobDefinitionId(Snowflake.Id(127)).hashCode()
                )
            }
        }
        @TestFactory
        fun Serialization() = generateTestCases(JobDefinitionId(Snowflake.Id(23)), "23")
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