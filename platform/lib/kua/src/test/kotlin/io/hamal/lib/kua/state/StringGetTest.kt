package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class StringGetTest : StateBaseTest() {

    @TestFactory
    fun `Reads value on stack without popping the value`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("hamal"))
        assertThat(testInstance.stringGet(1), equalTo(KuaString("hamal")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.stringPush(KuaString("rocks"))
        assertThat(testInstance.stringGet(2), equalTo(KuaString("rocks")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Reads value on stack with negative index without popping the value`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("hamal"))
        assertThat(testInstance.stringGet(1), equalTo(KuaString("hamal")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.stringPush(KuaString("rocks"))
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("rocks")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }
}