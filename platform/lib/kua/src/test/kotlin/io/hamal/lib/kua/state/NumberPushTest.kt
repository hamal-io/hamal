package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.numberGet
import io.hamal.lib.value.ValueNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class NumberPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val result = testInstance.numberPush(ValueNumber(23.23))
        assertThat(result, equalTo(StackTop(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.numberGet(1), equalTo(ValueNumber(23.23)))
    }

}