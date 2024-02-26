package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class TopTest : NativeBaseTest() {
    @Test
    fun `Nothing pushed on the stack`() {
        val result = testInstance.top()
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Pushing to the stack cause stack to grow`() {
        repeat(100) { idx ->
            assertThat(testInstance.top(), equalTo(idx))
            testInstance.pushBoolean(true)
        }
    }
}