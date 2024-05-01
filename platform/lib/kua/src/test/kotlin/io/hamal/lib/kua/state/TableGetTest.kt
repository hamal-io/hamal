package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.tableGet
import io.hamal.lib.kua.value.getString
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class TableGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        val tableOne = testInstance.tableCreate(ValueString("instance") to ValueString("One"))

        testInstance.tablePush(tableOne)
        assertThat(testInstance.tableGet(2).getString("instance"), equalTo(ValueString("One")))
        assertThat(testInstance.topGet(), equalTo(StackTop(3)))

        val tableTwo = testInstance.tableCreate(ValueString("instance") to ValueString("Two"))
        testInstance.tablePush(tableTwo)
        assertThat(testInstance.tableGet(4).getString("instance"), equalTo(ValueString("Two")))
        assertThat(testInstance.topGet(), equalTo(StackTop(6)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        val tableOne = testInstance.tableCreate(ValueString("instance") to ValueString("One"))

        testInstance.tablePush(tableOne)
        assertThat(testInstance.tableGet(2).getString("instance"), equalTo(ValueString("One")))
        assertThat(testInstance.topGet(), equalTo(StackTop(3)))

        val tableTwo = testInstance.tableCreate(ValueString("instance") to ValueString("Two"))
        testInstance.tablePush(tableTwo)

        assertThat(testInstance.tableGet(-1).getString("instance"), equalTo(ValueString("Two")))
        assertThat(testInstance.topGet(), equalTo(StackTop(6)))
    }

    @TestFactory
    fun `Not a table`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Not  a boolean"))
        assertThrows<IllegalStateException> { testInstance.tableGet(1) }
            .also { exception ->
                assertThat(
                    exception.message, equalTo("Expected type to be table but was string")
                )
            }
    }
}