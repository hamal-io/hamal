package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawGetTest : StateBaseTest() {

    @TestFactory
    fun `Gets value from table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("key"))
        testInstance.numberPush(ValueNumber(23.0))
        testInstance.tableRawSet(1)

        testInstance.stringPush(ValueString("key"))

        testInstance.tableRawGet(1).also { valueClass ->
            assertThat(valueClass, equalTo(ValueNumber::class))
        }

        assertThat(testInstance.numberGet(-1), equalTo(ValueNumber(23.0)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }
}