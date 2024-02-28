package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawSetIdxTest : StateBaseTest() {

    @TestFactory
    fun `Sets value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(1, 0)
        testInstance.stringPush(KuaString("value"))
        testInstance.tableRawSetIdx(1, 23)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(23.0))
        testInstance.tableRawGet(1).also { resultClass ->
            assertThat(resultClass, equalTo(KuaString::class))
        }
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("value")))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Replaces value in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("value"))
        testInstance.tableRawSetIdx(1, 23).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(42.0))
        testInstance.tableRawSetIdx(1, 23).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        testInstance.numberPush(KuaNumber(23.0))
        testInstance.tableRawGet(1).also { resultClass ->
            assertThat(resultClass, equalTo(KuaNumber::class))
        }
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(42.0)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Sets multiple values in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)

        testInstance.stringPush(KuaString("value"))
        testInstance.tableRawSetIdx(1, 2).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.numberPush(KuaNumber(42.0))
        testInstance.tableRawSetIdx(1, 4).also { table ->
            assertThat(table, equalTo(TableLength(2)))
        }

        testInstance.tableRawGetIdx(1, 2)
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("value")))

        testInstance.tableRawGetIdx(1, 4)
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(42.0)))

        assertThat(testInstance.tableLength(1), equalTo(TableLength(2)))
    }

}
