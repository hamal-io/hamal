package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.stringGet
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class SerdeStringGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("hamal"))
        assertThat(testInstance.stringGet(1), equalTo(ValueString("hamal")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.stringPush(ValueString("rocks"))
        assertThat(testInstance.stringGet(2), equalTo(ValueString("rocks")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("hamal"))
        assertThat(testInstance.stringGet(1), equalTo(ValueString("hamal")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.stringPush(ValueString("rocks"))
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("rocks")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a boolean`() = runTest { testInstance ->
        testInstance.numberPush(ValueNumber(42))
        assertThrows<IllegalStateException> {
            testInstance.stringGet(1)
        }.also { exception ->
            assertThat(
                exception.message,
                equalTo("Expected type to be string but was number")
            )
        }
    }

}