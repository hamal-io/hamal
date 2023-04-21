package io.hamal.lib.util

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

class SnowflakeTest {
    @Nested
    @DisplayName("DefaultTimeSource")
    inner class DefaultTimeSourceTest {
        @Test
        fun `Epoch is fixed`() {
            assertThat(DefaultTimeSource.epoch, equalTo(Instant.ofEpochMilli(1682116276624)))
        }

        @Test
        fun `elapsed() is monotonic`() {
            var prev = DefaultTimeSource.elapsed()
            for (i in (0..1_000_000)) {
                val current = DefaultTimeSource.elapsed()
                assertThat(prev, lessThanOrEqualTo(current))
                prev = current
            }
        }
    }

    @Nested
    @DisplayName("SnowflakeGenerator")
    inner class SnowflakeGeneratorTest {



    }
}