package io.hamal.lib.kua.state

import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.type.KuaNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableAppendTest : StateBaseTest() {

    @TestFactory
    fun `Append value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(KuaNumber(512.0))
        testInstance.tableAppend(1)

        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))

        testInstance.topPop(1)
    }

    @TestFactory
    fun `Append multiple values`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(KuaNumber(512.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        testInstance.numberPush(KuaNumber(1024.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(2)))
        }

        testInstance.numberPush(KuaNumber(2048.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(3)))
        }

        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
    }
}