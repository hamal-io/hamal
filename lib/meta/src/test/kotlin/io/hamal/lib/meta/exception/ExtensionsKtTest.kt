package io.hamal.lib.meta.exception

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Nested
class ExtensionsKtTest {


    @Nested
    @DisplayName("throwIf()")
    inner class ThrowIfTest {

        @Test
        fun `Throws if condition is true`() {
            val exception = assertThrows<IllegalArgumentException> {
                throwIf(true) {
                    IllegalArgumentException("TestMessage")
                }
            }
            assertThat(exception.message, equalTo("TestMessage"))
        }

        @Test
        fun `Does not throw if condition is false`() {
            throwIf(false) {
                IllegalArgumentException("NotThrown")
            }
        }

    }

}