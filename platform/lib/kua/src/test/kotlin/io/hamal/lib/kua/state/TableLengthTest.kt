package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableLengthTest : StateBaseTest() {

    @TestFactory
    fun `Length of empty table`() = runTest { testInstance ->
        testInstance.tableCreate(12, 12)
        val result = testInstance.tableLength(1)
        assertThat(result, equalTo(TableLength(0)))
    }

    @TestFactory
    fun `Length of table with appended values`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)

        testInstance.numberPush(KuaNumber(1.0))
        testInstance.tableAppend(1)
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))

        testInstance.numberPush(KuaNumber(2.0))
        testInstance.tableAppend(1)
        assertThat(testInstance.tableLength(1), equalTo(TableLength(2)))

        testInstance.numberPush(KuaNumber(3.0))
        testInstance.tableAppend(1)
        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
    }

    @TestFactory
    fun `Length of table with set values`() = runTest { testInstance ->
        testInstance.tableCreate()

        testInstance.tableCreate(0, 1)

        testInstance.stringPush(KuaString("value"))
        testInstance.tableFieldSet(2, KuaString("key"))
        assertThat(testInstance.tableLength(2), equalTo(TableLength(1)))

        testInstance.stringPush(KuaString("value"))
        testInstance.tableFieldSet(2, KuaString("key-2"))
        assertThat(testInstance.tableLength(2), equalTo(TableLength(2)))
    }

    @TestFactory
    fun `Length of sub table`() = runTest { testInstance ->
        val nested = testInstance.tableCreate(
            KuaString("hamal") to KuaString("rocks")
        )

        testInstance.tableCreate(
            KuaString("nested") to nested
        )

        testInstance.tableSubTableGet(-1, "nested").also { resultClass ->
            assertThat(resultClass, equalTo(KuaTable::class))
        }

        assertThat(testInstance.tableLength(-1), equalTo(TableLength(1)))
    }

}
