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

@DisplayName("TriggerId")
class TriggerIdTest {
    @Nested
    @DisplayName("equals()")
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
    @DisplayName("hashCode()")
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

@DisplayName("TriggerReference")
class TriggerRefTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                TriggerRef("some-ref"),
                TriggerRef("some-ref")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                TriggerRef("some-ref"),
                TriggerRef("another-ref")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TriggerRef("some-ref").hashCode(),
                TriggerRef("some-ref").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TriggerRef("some-ref").hashCode(),
                TriggerRef("another-ref").hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TriggerRef("some-ref").toString(), equalTo("TriggerRef(some-ref)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TriggerRef("some-ref"), "\"some-ref\"")
}

@DisplayName("InvokedTriggerId")
class InvokedTriggerIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                InvokedTriggerId(SnowflakeId(23)),
                InvokedTriggerId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                InvokedTriggerId(SnowflakeId(23)),
                InvokedTriggerId(SnowflakeId(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                InvokedTriggerId(SnowflakeId(23)).hashCode(),
                InvokedTriggerId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                InvokedTriggerId(SnowflakeId(23)).hashCode(),
                InvokedTriggerId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(InvokedTriggerId(SnowflakeId(123)).toString(), equalTo("InvokedTriggerId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(InvokedTriggerId(SnowflakeId(23)), "23")
}

