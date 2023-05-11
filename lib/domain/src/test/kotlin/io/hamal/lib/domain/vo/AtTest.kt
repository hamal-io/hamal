package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.InvokedAt
import io.hamal.lib.domain.vo.QueuedAt
import io.hamal.lib.domain.vo.ScheduledAt
import io.hamal.lib.domain.vo.helper.SerializationTestHelper
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.*
import java.time.Instant


@DisplayName("InvokedAt")
class InvokedAtTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            Assertions.assertEquals(
                InvokedAt(Instant.ofEpochMilli(123456)),
                InvokedAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            Assertions.assertNotEquals(
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
            Assertions.assertEquals(
                InvokedAt(Instant.ofEpochMilli(123456)).hashCode(),
                InvokedAt(Instant.ofEpochMilli(123456)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                InvokedAt(Instant.ofEpochMilli(123456)).hashCode(),
                InvokedAt(Instant.ofEpochMilli(654321)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        MatcherAssert.assertThat(
            InvokedAt(Instant.ofEpochMilli(123456)).toString(),
            CoreMatchers.equalTo("InvokedAt(1970-01-01T00:02:03.456Z)")
        )
    }

    @TestFactory
    fun Serialization() = SerializationTestHelper.generateTestCases(InvokedAt(Instant.ofEpochMilli(123456)), "123456")
}

@DisplayName("ScheduledAt")
class ScheduledAtTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            Assertions.assertEquals(
                ScheduledAt(Instant.ofEpochMilli(123456)),
                ScheduledAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            Assertions.assertNotEquals(
                ScheduledAt(Instant.ofEpochMilli(123456)),
                ScheduledAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            Assertions.assertEquals(
                ScheduledAt(Instant.ofEpochMilli(123456)).hashCode(),
                ScheduledAt(Instant.ofEpochMilli(123456)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                ScheduledAt(Instant.ofEpochMilli(123456)).hashCode(),
                ScheduledAt(Instant.ofEpochMilli(654321)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        MatcherAssert.assertThat(
            ScheduledAt(Instant.ofEpochMilli(123456)).toString(),
            CoreMatchers.equalTo("ScheduledAt(1970-01-01T00:02:03.456Z)")
        )
    }

    @TestFactory
    fun Serialization() = SerializationTestHelper.generateTestCases(ScheduledAt(Instant.ofEpochMilli(123456)), "123456")
}

@DisplayName("QueuedAt")
class QueuedAtTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            Assertions.assertEquals(
                QueuedAt(Instant.ofEpochMilli(123456)),
                QueuedAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            Assertions.assertNotEquals(
                QueuedAt(Instant.ofEpochMilli(123456)),
                QueuedAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            Assertions.assertEquals(
                QueuedAt(Instant.ofEpochMilli(123456)).hashCode(),
                QueuedAt(Instant.ofEpochMilli(123456)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                QueuedAt(Instant.ofEpochMilli(123456)).hashCode(),
                QueuedAt(Instant.ofEpochMilli(654321)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        MatcherAssert.assertThat(
            QueuedAt(Instant.ofEpochMilli(123456)).toString(),
            CoreMatchers.equalTo("QueuedAt(1970-01-01T00:02:03.456Z)")
        )
    }

    @TestFactory
    fun Serialization() = SerializationTestHelper.generateTestCases(QueuedAt(Instant.ofEpochMilli(123456)), "123456")
}