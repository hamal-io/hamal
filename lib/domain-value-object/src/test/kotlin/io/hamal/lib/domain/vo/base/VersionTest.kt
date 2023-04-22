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
class VersionTest {
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
}
