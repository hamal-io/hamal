package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.booleanGet
import io.hamal.lib.value.ValueFalse
import io.hamal.lib.value.ValueString
import io.hamal.lib.value.ValueTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class BooleanGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.booleanPush(ValueTrue)
        assertThat(testInstance.booleanGet(1), equalTo(ValueTrue))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.booleanPush(ValueFalse)
        assertThat(testInstance.booleanGet(2), equalTo(ValueFalse))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.booleanPush(ValueTrue)
        assertThat(testInstance.booleanGet(1), equalTo(ValueTrue))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.booleanPush(ValueFalse)
        assertThat(testInstance.booleanGet(-1), equalTo(ValueFalse))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a boolean`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Not  a boolean"))
        assertThrows<IllegalStateException> {
            testInstance.booleanGet(1)
        }.also { exception ->
            assertThat(
                exception.message,
                equalTo("Expected type to be boolean but was string")
            )
        }
    }

}