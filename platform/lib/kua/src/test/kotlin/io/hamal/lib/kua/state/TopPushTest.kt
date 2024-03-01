package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaDecimal
import io.hamal.lib.kua.type.KuaError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes a copy of the value on top of the stack`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("test"))
        testInstance.decimalPush(KuaDecimal(1))
        testInstance.decimalPush(KuaDecimal(2))
        testInstance.decimalPush(KuaDecimal(3))

        testInstance.topPush(1)
        assertThat(testInstance.topGet(), equalTo(StackTop(5)))
        assertThat(testInstance.type(-1), equalTo(KuaError::class))

        assertThat(testInstance.errorGet(5), equalTo(KuaError("test")))
    }
}