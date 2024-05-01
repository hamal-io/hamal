package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableSubTableGetTest : StateBaseTest() {

    @TestFactory
    fun `Get table from table`() = runTest { testInstance ->

        testInstance.tableCreate(
            ValueString("nested") to testInstance.tableCreate(
                ValueString("hamal") to ValueString("rocks")
            )
        )

        assertThat(testInstance.topGet(), equalTo(StackTop(2)))

        testInstance.tableSubTableGet(-1, "nested")
        assertThat(testInstance.topGet(), equalTo(StackTop(3)))
        assertThat(testInstance.type(-1), equalTo(KuaTable::class))
        assertThat(testInstance.tableLength(-1), equalTo(TableLength(1)))

        testInstance.tableFieldGet(-1, ValueString("hamal"))
        assertThat(testInstance.topGet(), equalTo(StackTop(4)))
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("rocks")))
    }

}