package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawGetTest : StateBaseTest() {

    @TestFactory
    fun `Gets value from table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("key"))
        testInstance.numberPush(KuaNumber(23.0))
        testInstance.tableRawSet(1)

        testInstance.stringPush(KuaString("key"))

        testInstance.tableRawGet(1).also { valueClass ->
            assertThat(valueClass, equalTo(KuaNumber::class))
        }

        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(23.0)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }
}