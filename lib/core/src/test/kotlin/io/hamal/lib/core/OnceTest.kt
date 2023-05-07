package io.hamal.lib.core

import io.hamal.lib.core.Once
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class OnceTest {

    @Nested
    inner class DefaultImplTest {

        private val once: Once<Any> = Once.default()

        @Test
        fun `First time execution`() {
            val result = once { 42 }

            assertThat(result, equalTo(42))
        }

        @Test
        fun `Already executed`() {
            `First time execution`()

            val result = once { 24 }
            assertThat(result, equalTo(42))
        }

    }
}