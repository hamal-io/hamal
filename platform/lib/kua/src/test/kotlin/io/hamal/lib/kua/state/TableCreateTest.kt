package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableCreateTest : StateBaseTest() {

    @TestFactory
    fun `Creates an empty table on empty stack`() = runTest { testInstance ->
        val result = testInstance.tableCreate(1, 2)
        assertThat(result.index, equalTo(ValueNumber(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))
    }

    @TestFactory
    fun `Creates array like table`() = runTest { testInstance ->
        val result = testInstance.tableCreate(
            listOf(
                ValueNumber(1),
                ValueNumber(2),
                ValueNumber(3)
            )
        )
        assertThat(result.index, equalTo(ValueNumber(1)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))
    }

    @TestFactory
    fun `Creates map like table`() = runTest { testInstance ->
        val result = testInstance.tableCreate(
            ValueString("key-1") to ValueString("value-1"),
            ValueString("key-2") to ValueString("value-2"),
            ValueString("key-3") to ValueString("value-3"),
        )
        assertThat(result.index, equalTo(ValueNumber(1)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))
    }
}