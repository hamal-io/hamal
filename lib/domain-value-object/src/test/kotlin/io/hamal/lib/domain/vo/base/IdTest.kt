package io.hamal.lib.domain.vo.base

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class IdTest {
    @Nested
    @DisplayName("IdValidator")
    inner class IdValidatorTest {
        @Nested
        @DisplayName("validate()")
        inner class ValidateTest {
            @Test
            fun `Does nothing if id is legal`() {
                IdValidator.validate("0x1337C0DE")
            }

            @Test
            fun `Throws IllegalArgumentException if id is not legal`() {
                val exception = assertThrows<IllegalArgumentException> {
                    IdValidator.validate("some-illegal-id")
                }
                assertThat(exception.message, containsString("Id('some-illegal-id') is illegal"))
            }
        }
    }
}
