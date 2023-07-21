package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.ValueType.Table
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class TableMapTest {

    @TestFactory
    fun set(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))

        return listOf(
            { testInstance.set("key", "value") },
            { testInstance.set("key", StringValue("value")) },
            { testInstance.set(StringValue("key"), StringValue("value")) },
            { testInstance.set("key", 23.23) },
            { testInstance.set("key", NumberValue(23.23)) },
            { testInstance.set(StringValue("key"), NumberValue(23.23)) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)

                assertThat("empty table", testInstance.length(), equalTo(TableLength(0)))

                val result = testFn()
                assertThat("item set", testInstance.length(), equalTo(TableLength(1)))
                assertThat(result, equalTo(TableLength(1)))

                assertThat("One element on stack", state.length(), equalTo(1))
                assertThat("Only table on stack", state.type(1), equalTo(Table))

                state.bridge.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    private val state = run {
        ResourceLoader.load()
        ClosableState(Sandbox().bridge)
    }

    private val bridge = state.bridge

    private fun verifyStackIsEmpty() {
        assertThat("Stack is empty", state.isEmpty(), equalTo(true))
    }
}