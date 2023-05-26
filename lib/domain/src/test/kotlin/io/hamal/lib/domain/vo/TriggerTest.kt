package io.hamal.lib.domain.vo

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.helper.SerializationFixture.generateTestCases
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory


class TriggerIdTest {
    @Nested

    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                TriggerId(SnowflakeId(23)),
                TriggerId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                TriggerId(SnowflakeId(23)),
                TriggerId(SnowflakeId(127))
            )
        }
    }

    @Nested

    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TriggerId(SnowflakeId(23)).hashCode(),
                TriggerId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TriggerId(SnowflakeId(23)).hashCode(),
                TriggerId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TriggerId(SnowflakeId(123)).toString(), equalTo("TriggerId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TriggerId(SnowflakeId(23)), "23")
}

class TriggerNameTest {
    @Nested

    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                TriggerName("some-ref"),
                TriggerName("some-ref")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                TriggerName("some-ref"),
                TriggerName("another-ref")
            )
        }
    }

    @Nested

    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TriggerName("some-ref").hashCode(),
                TriggerName("some-ref").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TriggerName("some-ref").hashCode(),
                TriggerName("another-ref").hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TriggerName("some-name").toString(), equalTo("TriggerName(some-name)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TriggerName("some-name"), "\"some-name\"")
}


class InvocationIdTest {
    @Nested

    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                InvocationId(SnowflakeId(23)),
                InvocationId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                InvocationId(SnowflakeId(23)),
                InvocationId(SnowflakeId(127))
            )
        }
    }

    @Nested

    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                InvocationId(SnowflakeId(23)).hashCode(),
                InvocationId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                InvocationId(SnowflakeId(23)).hashCode(),
                InvocationId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(InvocationId(SnowflakeId(123)).toString(), equalTo("InvocationId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(InvocationId(SnowflakeId(23)), "23")
}

