package io.hamal.lib.domain.vo

import io.hamal.lib.meta.exception.IllegalArgumentException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class ValidatorTest {
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
                assertThat(exception.localizedMessage, equalTo("Reference('$illegal') is illegal"))
            }
        }
    }

    @Nested
    @DisplayName("VersionValidator")
    inner class VersionValidatorTest {
        @Test
        fun `Does nothing if version is legal`() {
            VersionValidator.validate(1)
            VersionValidator.validate(2810)
            VersionValidator.validate(Int.MAX_VALUE)
        }

        @Test
        fun `Throws IllegalArgumentException if version is not legal`() {
            for (illegal in listOf(
                0,
                -1,
                Int.MIN_VALUE
            )) {
                val exception = assertThrows<IllegalArgumentException> {
                    VersionValidator.validate(illegal)
                }
                assertThat(exception.localizedMessage, equalTo("Version('$illegal') is illegal"))
            }
        }
    }

    @Nested
    @DisplayName("RegionValidator")
    inner class RegionValidatorTest {
        @Test
        fun `Does nothing if region is legal`() {
            RegionValidator.validate("1")
            RegionValidator.validate("abc")
            RegionValidator.validate("ABC")
            RegionValidator.validate("hamal-1")
            RegionValidator.validate("hamal_1-we")
            RegionValidator.validate("h".repeat(255))
        }

        @Test
        fun `Throws IllegalArgumentException if region is not legal`() {
            for (illegal in listOf(
                "hamal::4",
                "h@m@l",
                "h".repeat(256)
            )) {
                val exception = assertThrows<IllegalArgumentException> {
                    RegionValidator.validate(illegal)
                }
                assertThat(exception.localizedMessage, equalTo("Region('$illegal') is illegal"))
            }
        }
    }
}
