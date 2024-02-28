package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableFieldSetTest : StateBaseTest() {

    @TestFactory
    fun `Sets value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("value"))
        testInstance.tableFieldSet(1, KuaString("key"))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.tableFieldGet(1, KuaString("key"))
        assertThat(testInstance.stringGet(-1), equalTo(KuaString("value")))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Replace value in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("value"))
        testInstance.tableFieldSet(1, KuaString("key")).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.numberPush(KuaNumber(42.0))
        testInstance.tableFieldSet(1, KuaString("key")).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.tableFieldGet(1, KuaString("key"))
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(42.0)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Sets multiple values in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(KuaString("value"))
        testInstance.tableFieldSet(1, KuaString("key-1")).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(42.0))
        testInstance.tableFieldSet(1, KuaString("key-2")).also { table ->
            assertThat(table, equalTo(TableLength(2)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        assertThat(testInstance.tableLength(1), equalTo(TableLength(2)))
    }


}
