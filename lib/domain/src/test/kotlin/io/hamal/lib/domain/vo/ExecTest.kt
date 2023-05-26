package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.helper.SerializationFixture.generateTestCases
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory


class ExecIdTest {
    @Nested
    
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                ExecId(SnowflakeId(23)),
                ExecId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                ExecId(SnowflakeId(23)),
                ExecId(SnowflakeId(127))
            )
        }
    }

    @Nested
    
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                ExecId(SnowflakeId(23)).hashCode(),
                ExecId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                ExecId(SnowflakeId(23)).hashCode(),
                ExecId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(ExecId(SnowflakeId(123)).toString(), equalTo("ExecId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(ExecId(SnowflakeId(23)), "23")
}