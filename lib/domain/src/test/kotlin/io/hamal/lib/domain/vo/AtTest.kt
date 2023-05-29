package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.helper.SerializationFixture
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.time.Instant


class InvokedAtTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            Assertions.assertEquals(
                InvokedAt(Instant.ofEpochMilli(123456)),
                InvokedAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            Assertions.assertNotEquals(
                InvokedAt(Instant.ofEpochMilli(123456)),
                InvokedAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
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
    fun Serialization() = SerializationFixture.generateTestCases(InvokedAt(Instant.ofEpochMilli(123456)), "123456")
}


class ScheduledAtTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            Assertions.assertEquals(
                ScheduledAt(Instant.ofEpochMilli(123456)),
                ScheduledAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            Assertions.assertNotEquals(
                ScheduledAt(Instant.ofEpochMilli(123456)),
                ScheduledAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
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
    fun Serialization() = SerializationFixture.generateTestCases(ScheduledAt(Instant.ofEpochMilli(123456)), "123456")
}


class QueuedAtTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            Assertions.assertEquals(
                QueuedAt(Instant.ofEpochMilli(123456)),
                QueuedAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            Assertions.assertNotEquals(
                QueuedAt(Instant.ofEpochMilli(123456)),
                QueuedAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
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
    fun Serialization() = SerializationFixture.generateTestCases(QueuedAt(Instant.ofEpochMilli(123456)), "123456")
}


class CompletedAtTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            Assertions.assertEquals(
                CompletedAt(Instant.ofEpochMilli(123456)),
                CompletedAt(Instant.ofEpochMilli(123456))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            Assertions.assertNotEquals(
                CompletedAt(Instant.ofEpochMilli(123456)),
                CompletedAt(Instant.ofEpochMilli(654321))
            )
        }
    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            Assertions.assertEquals(
                CompletedAt(Instant.ofEpochMilli(123456)).hashCode(),
                CompletedAt(Instant.ofEpochMilli(123456)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            Assertions.assertNotEquals(
                CompletedAt(Instant.ofEpochMilli(123456)).hashCode(),
                CompletedAt(Instant.ofEpochMilli(654321)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        MatcherAssert.assertThat(
            CompletedAt(Instant.ofEpochMilli(123456)).toString(),
            CoreMatchers.equalTo("CompletedAt(1970-01-01T00:02:03.456Z)")
        )
    }

    @TestFactory
    fun Serialization() = SerializationFixture.generateTestCases(CompletedAt(Instant.ofEpochMilli(123456)), "123456")
}