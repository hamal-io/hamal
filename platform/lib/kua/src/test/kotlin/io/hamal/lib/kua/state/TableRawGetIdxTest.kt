package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableRawGetIdxTest : StateBaseTest() {

    @TestFactory
    fun `Gets value from table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.numberPush(KuaNumber(5.0))
        testInstance.stringPush(ValueString("value"))
        testInstance.tableRawSet(1)

        testInstance.tableRawGetIdx(1, 5).also { valueClass ->
            assertThat(valueClass, equalTo(ValueString::class))
        }
        assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }
}