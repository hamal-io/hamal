package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableFieldSetTest : StateBaseTest() {

    @TestFactory
    fun `Sets value to empty table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("value"))
        testInstance.tableFieldSet(1, ValueString("key"))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.tableFieldGet(1, ValueString("key"))
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Replace value in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("value"))
        testInstance.tableFieldSet(1, ValueString("key")).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.numberPush(ValueNumber(42.0))
        testInstance.tableFieldSet(1, ValueString("key")).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }

        testInstance.tableFieldGet(1, ValueString("key"))
        assertThat(testInstance.numberGet(-1), equalTo(ValueNumber(42.0)))
        assertThat(testInstance.tableLength(1), equalTo(TableLength(1)))
    }

    @TestFactory
    fun `Sets multiple values in table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("value"))
        testInstance.tableFieldSet(1, ValueString("key-1")).also { table ->
            assertThat(table, equalTo(TableLength(1)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(ValueNumber(42.0))
        testInstance.tableFieldSet(1, ValueString("key-2")).also { table ->
            assertThat(table, equalTo(TableLength(2)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        assertThat(testInstance.tableLength(1), equalTo(TableLength(2)))
    }
}
