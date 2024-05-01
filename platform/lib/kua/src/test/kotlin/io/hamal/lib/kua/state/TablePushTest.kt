package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getString
import io.hamal.lib.common.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TablePushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val table = testInstance.tableCreate(ValueString("message") to ValueString("secret"))

        testInstance.tablePush(table)

        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
        assertThat(testInstance.type(2), equalTo(KuaTable::class))

        KuaTable(2, testInstance).getString("message").also { result ->
            assertThat(result, equalTo(ValueString("secret")))
        }
    }


}