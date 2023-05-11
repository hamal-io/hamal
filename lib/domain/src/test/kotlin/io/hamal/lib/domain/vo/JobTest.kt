package io.hamal.lib.domain.vo

import io.hamal.lib.domain.util.SnowflakeId
import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.helper.SerializationTestHelper.generateTestCases
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

@DisplayName("JobId")
class JobIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                JobId(SnowflakeId(23)),
                JobId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                JobId(SnowflakeId(23)),
                JobId(SnowflakeId(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                JobId(SnowflakeId(23)).hashCode(),
                JobId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                JobId(SnowflakeId(23)).hashCode(),
                JobId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(JobId(SnowflakeId(123)).toString(), equalTo("JobId(123)"))
    }


    @TestFactory
    fun Serialization() = generateTestCases(JobId(SnowflakeId(23)), "23")
}

@DisplayName("JobDefinitionId")
class JobDefinitionIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                JobDefinitionId(SnowflakeId(23)),
                JobDefinitionId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                JobDefinitionId(SnowflakeId(23)),
                JobDefinitionId(SnowflakeId(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                JobDefinitionId(SnowflakeId(23)).hashCode(),
                JobDefinitionId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                JobDefinitionId(SnowflakeId(23)).hashCode(),
                JobDefinitionId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(JobDefinitionId(SnowflakeId(123)).toString(), equalTo("JobDefinitionId(123)"))
    }


    @TestFactory
    fun Serialization() = generateTestCases(JobDefinitionId(SnowflakeId(23)), "23")
}

@DisplayName("JobReference")
class JobReferenceTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                JobReference("some-ref"),
                JobReference("some-ref")
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                JobReference("some-ref"),
                JobReference("another-ref")
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                JobReference("some-ref").hashCode(),
                JobReference("some-ref").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                JobReference("some-ref").hashCode(),
                JobReference("another-ref").hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(JobReference("some-ref").toString(), equalTo("JobReference(some-ref)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(JobReference("some-ref"), "\"some-ref\"")
}
