package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TablePushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val table = testInstance.tableCreate("message" to KuaString("secret"))

        testInstance.tablePush(table)

        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
        assertThat(testInstance.type(2), equalTo(KuaTable::class))

        KuaTable(2, testInstance).getString("message").also { result ->
            assertThat(result, equalTo("secret"))
        }
    }


}