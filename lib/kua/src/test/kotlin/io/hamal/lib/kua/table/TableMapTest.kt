package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.value.NilValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.TrueValue
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
            { testInstance.set("key", true) },
            { testInstance.set("key", TrueValue) },
            { testInstance.set(StringValue("key"), TrueValue) },

            { testInstance.set("key", 23.23) },
            { testInstance.set("key", NumberValue(23.23)) },
            { testInstance.set(StringValue("key"), NumberValue(23.23)) },

            { testInstance.set("key", "value") },
            { testInstance.set("key", StringValue("value")) },
            { testInstance.set(StringValue("key"), StringValue("value")) }
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


    @TestFactory
    fun unset(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))
        return listOf(
            { testInstance.unset("key") },
            { testInstance.unset(StringValue("key")) },
            { testInstance.set("key", NilValue) },
            { testInstance.set(StringValue("key"), NilValue) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)

                testInstance["key"] = "value"
                testInstance["another-key"] = "another-value"
                assertThat(testInstance.length(), equalTo(TableLength(2)))

                testFn()
                assertThat(testInstance.length(), equalTo(TableLength(1)))

                assertThat("One element on stack", state.length(), equalTo(1))
                assertThat("Only table on stack", state.type(1), equalTo(Table))

                state.bridge.tableGetField(1, "another-key")
                assertThat(state.getString(-1), equalTo("another-value"))

                state.bridge.pop(2)
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