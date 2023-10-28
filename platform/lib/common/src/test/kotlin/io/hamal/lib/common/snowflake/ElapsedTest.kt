package io.hamal.lib.common.snowflake

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.lessThanOrEqualTo
import org.junit.jupiter.api.Test

internal class ElapsedSourceTest {
    @Test
    fun `With default epoch`() {
        val testInstance = ElapsedSourceImpl()
        assertThat(testInstance.epoch, equalTo(1698451200000L))
    }

    @Test
    fun `Is monotonic`() {
        val testInstance = ElapsedSourceImpl()
        var prev = testInstance.elapsed()
        for (i in (0..1_000_000)) {
            val current = testInstance.elapsed()
            assertThat(prev, lessThanOrEqualTo(current))
            prev = current
        }
    }
}