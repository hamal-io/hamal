package io.hamal.lib.kua.state

import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.type.KuaNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableLengthTest : StateBaseTest() {

    @TestFactory
    fun `Size of empty table`() = runTest { testInstance ->
        testInstance.tableCreate(12, 12)
        val result = testInstance.tableLength(1)
        assertThat(result, equalTo(TableLength(0)))
    }

    @TestFactory
    fun `Size of table with fields`() = runTest { testInstance ->
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

}