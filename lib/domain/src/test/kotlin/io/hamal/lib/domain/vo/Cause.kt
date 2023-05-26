package io.hamal.lib.domain.vo

import io.hamal.lib.domain.vo.helper.SerializationFixture.generateTestCases
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*


class CodeTest {
    @Nested
    
    inner class EqualsTest {
        @Test
        fun `Equal if underlying values are equal`() {
            assertEquals(
                Code("Hamal='rocks'"),
                Code("Hamal='rocks'")
            )
        }

        @Test
        fun `Not Equal if underlying values are different`() {
            assertNotEquals(
                Code("Hamal='rocks'"),
                Code("Hamal='sucks'")
            )
        }
    }

    @Nested
    
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if values are equal`() {
            assertEquals(
                Code("Hamal='rocks'").hashCode(),
                Code("Hamal='rocks'").hashCode()
            )
        }

        @Test
        fun `Different hashcode if values are different`() {
            assertNotEquals(
                Code("Hamal='rocks'").hashCode(),
                Code("Hamal='sucks'").hashCode()
            )
        }
    }

    @TestFactory
    fun Serialization() = generateTestCases(Code("hamal='rocks'"), "\"hamal='rocks'\"")
}

