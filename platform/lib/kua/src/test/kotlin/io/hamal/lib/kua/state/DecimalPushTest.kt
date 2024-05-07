package io.hamal.lib.kua.state

import io.hamal.lib.common.value.ValueDecimal
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.decimalGet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class DecimalPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val result = testInstance.decimalPush(ValueDecimal("23.23"))
        assertThat(result, equalTo(StackTop(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal("23.23")))
    }

}