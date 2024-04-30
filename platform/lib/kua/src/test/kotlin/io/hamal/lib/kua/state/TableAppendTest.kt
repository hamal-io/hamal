package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.value.ValueNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableAppendTest : StateBaseTest() {

    @TestFactory
    fun `Append value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(ValueNumber(512.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Append multiple values`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(ValueNumber(512.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        testInstance.numberPush(ValueNumber(1024.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(2)))
        }

        testInstance.numberPush(ValueNumber(2048.0))
        testInstance.tableAppend(1).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(3)))
        }

        assertThat(testInstance.tableLength(1), equalTo(TableLength(3)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }
}