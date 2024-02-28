package io.hamal.lib.kua.state

import io.hamal.lib.kua.TableLength
import io.hamal.lib.kua.type.KuaNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableAppendTest : StateBaseTest() {

    @TestFactory
    fun `Insert value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(KuaNumber(512.0))
        testInstance.tableAppend(1)

        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))

        testInstance.topPop(1)
    }
}