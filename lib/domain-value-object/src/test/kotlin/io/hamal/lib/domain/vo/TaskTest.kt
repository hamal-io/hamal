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
class TaskTest {

    @Nested
    @DisplayName("TaskId")
    inner class TaskIdTest {
        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    TaskId(Snowflake.Id(23)),
                    TaskId(Snowflake.Id(23))
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    TaskId(Snowflake.Id(23)),
                    TaskId(Snowflake.Id(127))
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    TaskId(Snowflake.Id(23)).hashCode(),
                    TaskId(Snowflake.Id(23)).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    TaskId(Snowflake.Id(23)).hashCode(),
                    TaskId(Snowflake.Id(127)).hashCode()
                )
            }
        }

        @TestFactory
        fun Serialization() = generateTestCases(TaskId(Snowflake.Id(23)), "23")
    }
}