package io.hamal.lib.domain.vo

import io.hamal.lib.domain.util.SnowflakeId
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.helper.SerializationTestHelper.generateTestCases
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

@DisplayName("TenantId")
class TenantIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                TenantId(SnowflakeId(23)),
                TenantId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                TenantId(SnowflakeId(23)),
                TenantId(SnowflakeId(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
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
