package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopPopTest : StateBaseTest() {

    @TestFactory
    fun `Pops elements from stack`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal(1.0))
        testInstance.decimalPush(KuaDecimal(2.0))
        testInstance.decimalPush(KuaDecimal(3.0))
        testInstance.decimalPush(KuaDecimal(4.0))

        val result = testInstance.topPop(2)
        assertThat(result, equalTo(StackTop(2)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))

        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal(1.0)))
        assertThat(testInstance.decimalGet(2), equalTo(KuaDecimal(2.0)))
    }

}