package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
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

    @TestFactory
    fun `Creates array like table`() = runTest { testInstance ->
        val result = testInstance.tableCreate(
            listOf(
                KuaNumber(1),
                KuaNumber(2),
                KuaNumber(3)
            )
        )
        assertThat(result.index, equalTo(1))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))
    }

    @TestFactory
    fun `Creates map like table`() = runTest { testInstance ->
        val result = testInstance.tableCreate(
            KuaString("key-1") to KuaString("value-1"),
            KuaString("key-2") to KuaString("value-2"),
            KuaString("key-3") to KuaString("value-3"),
        )
        assertThat(result.index, equalTo(1))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))
    }
}