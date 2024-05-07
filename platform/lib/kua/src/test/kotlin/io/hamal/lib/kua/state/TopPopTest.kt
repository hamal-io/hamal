package io.hamal.lib.kua.state

import io.hamal.lib.common.value.ValueDecimal
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.decimalGet
import io.hamal.lib.kua.topPop
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopPopTest : StateBaseTest() {

    @TestFactory
    fun `Pops elements from stack`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(1.0))
        testInstance.decimalPush(ValueDecimal(2.0))
        testInstance.decimalPush(ValueDecimal(3.0))
        testInstance.decimalPush(ValueDecimal(4.0))

        val result = testInstance.topPop(2)
        assertThat(result, equalTo(StackTop(2)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))

        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal(1.0)))
        assertThat(testInstance.decimalGet(2), equalTo(ValueDecimal(2.0)))
    }

}