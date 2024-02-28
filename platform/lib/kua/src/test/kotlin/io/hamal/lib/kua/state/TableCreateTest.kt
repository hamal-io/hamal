package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableCreateTest : StateBaseTest() {

    @TestFactory
    fun `Creates an empty table on empty stack`() = runTest { testInstance ->
        val result = testInstance.tableCreate(1, 2)
        assertThat(result.index, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))
    }

}