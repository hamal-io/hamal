package io.hamal.lib.domain.vo.base

import io.hamal.lib.meta.exception.IllegalArgumentException
import org.hamcrest.CoreMatchers.equalTo
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
                IdValidator.validate("410908f7-7e1c-4556-81d9-a29a5e5f42a3")
            }

            @Test
            fun `Throws IllegalArgumentException if id is not legal`() {
                val exception = assertThrows<IllegalArgumentException> {
                    IdValidator.validate("some-illegal-id")
                }
                assertThat(exception.localizedMessage, equalTo("Id('some-illegal-id') is illegal"))
            }
        }
    }
}
