package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawGetIdxTest : StateBaseTest() {

    @TestFactory
    fun `Gets value from table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.numberPush(KuaNumber(5.0))
        testInstance.stringPush(KuaString("value"))
        testInstance.tableRawSet(1)

        testInstance.tableRawGetIdx(1, 5).also { valueClass ->
            assertThat(valueClass, equalTo(KuaString::class))
        }
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("value")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }
}