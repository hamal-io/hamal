package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawSetIdxTest : StateBaseTest() {

    @TestFactory
    fun `Sets value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(1, 0)
        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSetIdx(1, 23)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(ValueNumber(23.0))
        testInstance.tableRawGet(1).also { valueClass ->
            assertThat(valueClass, equalTo(ValueString::class))
        }
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Replaces value in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSetIdx(1, 23).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(ValueNumber(42.0))
        testInstance.tableRawSetIdx(1, 23).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        testInstance.numberPush(ValueNumber(23.0))
        testInstance.tableRawGet(1).also { valueClass ->
            assertThat(valueClass, equalTo(ValueNumber::class))
        }
        assertThat(testInstance.numberGet(-1), equalTo(ValueNumber(42.0)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Sets multiple values in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)

        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSetIdx(1, 2).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.numberPush(ValueNumber(42.0))
        testInstance.tableRawSetIdx(1, 4).also { table ->
            assertThat(table, equalTo(TableLength(2)))
        }

        testInstance.tableRawGetIdx(1, 2)
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))

        testInstance.tableRawGetIdx(1, 4)
        assertThat(testInstance.numberGet(-1), equalTo(ValueNumber(42.0)))

        assertThat(testInstance.tableLength(1), equalTo(TableLength(2)))
    }

}
