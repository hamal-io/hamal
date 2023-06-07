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


class AccountIdTest {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                AccountId(SnowflakeId(23)),
                AccountId(SnowflakeId(23))
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                AccountId(SnowflakeId(23)),
                AccountId(SnowflakeId(127))
            )
        }
    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                AccountId(SnowflakeId(23)).hashCode(),
                AccountId(SnowflakeId(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                AccountId(SnowflakeId(23)).hashCode(),
                AccountId(SnowflakeId(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(AccountId(SnowflakeId(123)).toString(), equalTo("AccountId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(AccountId(SnowflakeId(23)), "23")
}
