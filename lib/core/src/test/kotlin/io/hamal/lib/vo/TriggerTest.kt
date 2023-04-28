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
import java.time.Instant

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

@DisplayName("TriggerReference")
class TriggerReferenceTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                TriggerReference("some-ref"),
                TriggerReference("some-ref")
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                TriggerReference("some-ref"),
                TriggerReference("another-ref")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TriggerReference("some-ref").hashCode(),
                TriggerReference("some-ref").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TriggerReference("some-ref").hashCode(),
                TriggerReference("another-ref").hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TriggerReference("some-ref").toString(), equalTo("TriggerReference(some-ref)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TriggerReference("some-ref"), "\"some-ref\"")
}

@DisplayName("InvokedTriggerId")
class InvokedTriggerIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                InvokedTriggerId(Snowflake.Id(23)),
                InvokedTriggerId(Snowflake.Id(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                InvokedTriggerId(Snowflake.Id(23)),
                InvokedTriggerId(Snowflake.Id(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                InvokedTriggerId(Snowflake.Id(23)).hashCode(),
                InvokedTriggerId(Snowflake.Id(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                InvokedTriggerId(Snowflake.Id(23)).hashCode(),
                InvokedTriggerId(Snowflake.Id(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(InvokedTriggerId(Snowflake.Id(123)).toString(), equalTo("InvokedTriggerId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(InvokedTriggerId(Snowflake.Id(23)), "23")
}

@DisplayName("InvokedAt")
class InvokedAtTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                InvokedAt(Instant.ofEpochMilli(123456)),
                InvokedAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                InvokedAt(Instant.ofEpochMilli(123456)),
                InvokedAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                InvokedAt(Instant.ofEpochMilli(123456)).hashCode(),
                InvokedAt(Instant.ofEpochMilli(123456)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                InvokedAt(Instant.ofEpochMilli(123456)).hashCode(),
                InvokedAt(Instant.ofEpochMilli(654321)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(InvokedAt(Instant.ofEpochMilli(123456)).toString(), equalTo("InvokedAt(1970-01-01T00:02:03.456Z)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(InvokedAt(Instant.ofEpochMilli(123456)), "123456")
}