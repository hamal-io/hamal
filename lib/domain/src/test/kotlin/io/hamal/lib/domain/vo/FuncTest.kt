package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.fixture.SerializationFixture.generateTestCases
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory


class FuncIdTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                FuncId(SnowflakeId(23)),
                FuncId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                FuncId(SnowflakeId(23)),
                FuncId(SnowflakeId(127))
            )
        }
    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                FuncId(SnowflakeId(23)).hashCode(),
                FuncId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                FuncId(SnowflakeId(23)).hashCode(),
                FuncId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(FuncId(SnowflakeId(123)).toString(), equalTo("FuncId(123)"))
    }


    @TestFactory
    fun serialization() = generateTestCases(FuncId(SnowflakeId(23)), "23")
}


class FuncRefTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                FuncName("some-ref"),
                FuncName("some-ref")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                FuncName("some-ref"),
                FuncName("another-ref")
            )
        }
    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                FuncName("some-ref").hashCode(),
                FuncName("some-ref").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                FuncName("some-ref").hashCode(),
                FuncName("another-ref").hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(FuncName("some-ref").toString(), equalTo("FuncName(some-ref)"))
    }

    @TestFactory
    fun serialization() = generateTestCases(FuncName("some-ref"), "\"some-ref\"")
}
