package io.hamal.lib.vo

import io.hamal.lib.util.Snowflake
import io.hamal.lib.vo.helper.SerializationTestHelper.generateTestCases
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

@DisplayName("TriggerId")
class TriggerIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                TriggerId(Snowflake.Id(23)),
                TriggerId(Snowflake.Id(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                TriggerId(Snowflake.Id(23)),
                TriggerId(Snowflake.Id(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TriggerId(Snowflake.Id(23)).hashCode(),
                TriggerId(Snowflake.Id(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TriggerId(Snowflake.Id(23)).hashCode(),
                TriggerId(Snowflake.Id(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TriggerId(Snowflake.Id(123)).toString(), equalTo("TriggerId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TriggerId(Snowflake.Id(23)), "23")
}
