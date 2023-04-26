package io.hamal.lib.domain.vo.base

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class ReferenceTest {
    @Nested
    @DisplayName("ReferenceValidator")
    inner class ReferenceValidatorTest {
        @Test
        fun `Does nothing if reference is legal`() {
            ReferenceValidator.validate("hamal")
            ReferenceValidator.validate("ha-mal")
            ReferenceValidator.validate("ha_mal")
            ReferenceValidator.validate("HamaL")
            ReferenceValidator.validate("h")
            ReferenceValidator.validate("H4m41")
            ReferenceValidator.validate("H4:m::41")
            ReferenceValidator.validate("H@M@1")
            ReferenceValidator.validate("H".repeat(255))
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
                    ReferenceValidator.validate(illegal)
                }
                assertThat(exception.message, containsString("Reference('$illegal') is illegal"))
            }
        }
    }
}
