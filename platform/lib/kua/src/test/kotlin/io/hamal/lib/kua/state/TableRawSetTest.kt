package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawSetTest : StateBaseTest() {

    @TestFactory
    fun `Sets value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("key"))
        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSet(1)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.stringPush(ValueString("key"))
        testInstance.tableRawGet(1)
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Replace value in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("key"))
        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSet(1).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.stringPush(ValueString("key"))
        testInstance.numberPush(KuaNumber(42.0))
        testInstance.tableRawSet(1).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }


        testInstance.tableFieldGet(1, ValueString("key"))
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(42.0)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Sets multiple values in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)

        testInstance.stringPush(ValueString("key"))
        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSet(1).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.stringPush(ValueString("different"))
        testInstance.numberPush(KuaNumber(42.0))
        testInstance.tableRawSet(1).also { table ->
            assertThat(table, equalTo(TableLength(2)))
        }

        testInstance.tableFieldGet(1, ValueString("key"))
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))

        testInstance.tableFieldGet(1, ValueString("different"))
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(42.0)))

        assertThat(testInstance.tableLength(1), equalTo(TableLength(2)))
    }
}