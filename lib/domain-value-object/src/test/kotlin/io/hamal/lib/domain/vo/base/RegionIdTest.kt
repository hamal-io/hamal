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
class RegionIdTest {
    @Nested
    @DisplayName("RegionIdValidator")
    inner class RegionIdValidatorTest {
        @Test
        fun `Does nothing if region is legal`() {
            RegionIdValidator.validate("1")
            RegionIdValidator.validate("abc")
            RegionIdValidator.validate("ABC")
            RegionIdValidator.validate("hamal-1")
            RegionIdValidator.validate("hamal_1-we")
            RegionIdValidator.validate("h".repeat(255))
        }

        @Test
        fun `Throws IllegalArgumentException if region is not legal`() {
            for (illegal in listOf(
                "hamal::4",
                "h@m@l",
                "h".repeat(256)
            )) {
                val exception = assertThrows<IllegalArgumentException> {
                    RegionIdValidator.validate(illegal)
                }
                assertThat(exception.localizedMessage, equalTo("Region('$illegal') is illegal"))
            }
        }
    }
}
