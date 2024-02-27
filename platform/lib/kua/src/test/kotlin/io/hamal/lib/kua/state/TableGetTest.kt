package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableGetTest : StateBaseTest() {

    @TestFactory
    fun `Reads value on stack without popping the value`() = runTest { testInstance ->
        val tableOne = testInstance.tableCreate("instance" to KuaString("One"))

        testInstance.tablePush(tableOne)
        assertThat(testInstance.tableGet(2).getStringType("instance"), equalTo(KuaString("One")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))

        val tableTwo = testInstance.tableCreate("instance" to KuaString("Two"))
        testInstance.tablePush(tableTwo)
        assertThat(testInstance.tableGet(4).getStringType("instance"), equalTo(KuaString("Two")))
        assertThat(testInstance.topGet(), equalTo(StackTop(4)))
    }

    @TestFactory
    fun `Reads value on stack with negative index without popping the value`() = runTest { testInstance ->
        val tableOne = testInstance.tableCreate("instance" to KuaString("One"))

        testInstance.tablePush(tableOne)
        assertThat(testInstance.tableGet(2).getStringType("instance"), equalTo(KuaString("One")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))

        val tableTwo = testInstance.tableCreate("instance" to KuaString("Two"))
        testInstance.tablePush(tableTwo)
        assertThat(testInstance.tableGet(-1).getStringType("instance"), equalTo(KuaString("Two")))
        assertThat(testInstance.topGet(), equalTo(StackTop(4)))
    }
}