package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.booleanGet
import io.hamal.lib.kua.type.KuaFalse
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class BooleanGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.booleanPush(KuaTrue)
        assertThat(testInstance.booleanGet(1), equalTo(KuaTrue))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.booleanPush(KuaFalse)
        assertThat(testInstance.booleanGet(2), equalTo(KuaFalse))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.booleanPush(KuaTrue)
        assertThat(testInstance.booleanGet(1), equalTo(KuaTrue))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.booleanPush(KuaFalse)
        assertThat(testInstance.booleanGet(-1), equalTo(KuaFalse))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a boolean`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("Not  a boolean"))
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