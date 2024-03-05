package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableFieldGetTest : StateBaseTest() {

    @TestFactory
    fun `Gets value from table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("value"))
        testInstance.tableFieldSet(1, KuaString("key"))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.tableFieldGet(1, KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaString::class))
            assertThat(testInstance.stringGet(-1), equalTo(KuaString("value")))
            assertThat(testInstance.topGet(), equalTo(StackTop(2)))
        }
    }
}