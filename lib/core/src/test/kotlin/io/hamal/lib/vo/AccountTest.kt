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

@DisplayName("AccountId")
class AccountIdTest {
    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if underlying values are equal`() {
            assertEquals(
                AccountId(Snowflake.Id(23)),
                AccountId(Snowflake.Id(23))
            )
        }

        @Test
        fun `Not equals if underlying values are different`() {
            assertNotEquals(
                AccountId(Snowflake.Id(23)),
                AccountId(Snowflake.Id(127))
            )
        }
    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                AccountId(Snowflake.Id(23)).hashCode(),
                AccountId(Snowflake.Id(23)).hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                AccountId(Snowflake.Id(23)).hashCode(),
                AccountId(Snowflake.Id(127)).hashCode()
            )
        }
    }

    @Test
    fun `toString override`() {
        assertThat(AccountId(Snowflake.Id(123)).toString(), equalTo("AccountId(123)"))
    }

    @TestFactory
    fun Serialization() = generateTestCases(AccountId(Snowflake.Id(23)), "23")
}
