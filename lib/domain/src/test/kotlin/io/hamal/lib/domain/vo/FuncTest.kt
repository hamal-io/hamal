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


@DisplayName("FuncId")
class FuncIdTest {
    @Nested
    @DisplayName("equals()")
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
    @DisplayName("hashCode()")
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
    fun Serialization() = generateTestCases(FuncId(SnowflakeId(23)), "23")
}

@DisplayName("FuncRef")
class FuncRefTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                FuncRef("some-ref"),
                FuncRef("some-ref")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                FuncRef("some-ref"),
                FuncRef("another-ref")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                FuncRef("some-ref").hashCode(),
                FuncRef("some-ref").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                FuncRef("some-ref").hashCode(),
                FuncRef("another-ref").hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(FuncRef("some-ref").toString(), equalTo("FuncRef(some-ref)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(FuncRef("some-ref"), "\"some-ref\"")
}
