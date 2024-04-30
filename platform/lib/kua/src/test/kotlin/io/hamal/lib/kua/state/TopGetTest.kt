package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.value.ValueDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopGetTest : StateBaseTest() {

    @TestFactory
    fun `Nothing pushed on the stack`() = runTest { testInstance ->
        val result = testInstance.topGet()
        assertThat(result, equalTo(StackTop(0)))
    }

    @TestFactory
    fun `Pushing to the stack cause stack to grow`() = runTest { testInstance ->
        repeat(100) { idx ->
            assertThat(testInstance.topGet(), equalTo(StackTop(idx)))
            testInstance.decimalPush(ValueDecimal(123))
        }
    }
}