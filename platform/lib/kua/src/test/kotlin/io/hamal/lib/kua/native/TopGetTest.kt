package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class TopGetTest : NativeBaseTest() {
    @Test
    fun `Nothing pushed on the stack`() {
        val result = testInstance.topGet()
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Pushing to the stack cause stack to grow`() {
        repeat(100) { idx ->
            assertThat(testInstance.topGet(), equalTo(idx))
            testInstance.booleanPush(true)
        }
    }
}