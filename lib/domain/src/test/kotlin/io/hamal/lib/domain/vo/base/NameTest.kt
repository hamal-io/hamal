package io.hamal.lib.domain.vo.base

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class NameTest {
    @Nested
    
    inner class DomainNameValidatorTest {
        @Test
        fun `Does nothing if reference is legal`() {
            DomainNameValidator.validate("hamal")
            DomainNameValidator.validate("ha-mal")
            DomainNameValidator.validate("ha_mal")
            DomainNameValidator.validate("HamaL")
            DomainNameValidator.validate("h")
            DomainNameValidator.validate("H4m41")
            DomainNameValidator.validate("H4:m::41")
            DomainNameValidator.validate("H@M@1")
            DomainNameValidator.validate("H".repeat(255))
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
                    DomainNameValidator.validate(illegal)
                }
                assertThat(exception.message, containsString("Reference('$illegal') is illegal"))
            }
        }
    }
}
