package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.common.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TableFieldGetTest : StateBaseTest() {

    @TestFactory
    fun `Gets value from table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 1)
        testInstance.stringPush(ValueString("value"))
        testInstance.tableFieldSet(1, ValueString("key"))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.tableFieldGet(1, ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueString::class))
            assertThat(testInstance.stringGet(-1), equalTo(ValueString("value")))
            assertThat(testInstance.topGet(), equalTo(StackTop(2)))
        }
    }
}