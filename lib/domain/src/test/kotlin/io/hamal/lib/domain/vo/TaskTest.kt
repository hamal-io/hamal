package io.hamal.lib.domain.vo

import io.hamal.lib.domain.util.SnowflakeId
import io.hamal.lib.domain.vo.TaskId
import io.hamal.lib.domain.vo.helper.SerializationTestHelper.generateTestCases
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

@DisplayName("TaskId")
class TaskIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                TaskId(SnowflakeId(23)),
                TaskId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                TaskId(SnowflakeId(23)),
                TaskId(SnowflakeId(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TaskId(SnowflakeId(23)).hashCode(),
                TaskId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TaskId(SnowflakeId(23)).hashCode(),
                TaskId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TaskId(SnowflakeId(123)).toString(), equalTo("TaskId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TaskId(SnowflakeId(23)), "23")
}
