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


class TenantIdTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                TenantId(SnowflakeId(23)),
                TenantId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                TenantId(SnowflakeId(23)),
                TenantId(SnowflakeId(127))
            )
        }
    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                TenantId(SnowflakeId(23)).hashCode(),
                TenantId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                TenantId(SnowflakeId(23)).hashCode(),
                TenantId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(TenantId(SnowflakeId(123)).toString(), equalTo("TenantId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(TenantId(SnowflakeId(23)), "23")
}
