package io.hamal.lib.domain.vo.base

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class RefTest {
    @Nested
    @DisplayName("ReferenceValidator")
    inner class RefValidatorTest {
        @Test
        fun `Does nothing if reference is legal`() {
            RefValidator.validate("hamal")
            RefValidator.validate("ha-mal")
            RefValidator.validate("ha_mal")
            RefValidator.validate("HamaL")
            RefValidator.validate("h")
            RefValidator.validate("H4m41")
            RefValidator.validate("H4:m::41")
            RefValidator.validate("H@M@1")
            RefValidator.validate("H".repeat(255))
        }

        @Test
        fun `Throws IllegalArgumentException if reference is not legal`() {
            for (illegal in listOf(
                "",
                " hamal",
                "hamal ",
                "h".repeat(256),
            )) {
                val exception = assertThrows<IllegalArgumentException> {
                    RefValidator.validate(illegal)
                }
                assertThat(exception.message, containsString("Reference('$illegal') is illegal"))
            }
        }
    }
}
