package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.ResourceLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.value.*
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

            { testInstance.set("key", CodeValue("print('hacked')")) },
            { testInstance.set(StringValue("key"), CodeValue("print('hacked')")) },

            { testInstance.set("key", 23) },
            { testInstance.set("key", 23f) },
            { testInstance.set("key", 23L) },
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

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
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

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(Table))

                state.bridge.tableGetField(1, "another-key")
                assertThat(state.getString(-1), equalTo("another-value"))

                state.bridge.pop(2)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getBooleanValue(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))
        return listOf(
            { testInstance.getBooleanValue("key") },
            { testInstance.getBooleanValue(StringValue("key")) },
            { testInstance.getBoolean("key") },
            { testInstance.getBoolean(StringValue("key")) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)
                testInstance["key"] = true

                when (val result = testFn()) {
                    is BooleanValue -> assertThat(result, equalTo(TrueValue))
                    is Boolean -> assertThat(result, equalTo(true))
                    else -> TODO()
                }

                assertThat(testInstance.length(), equalTo(TableLength(1)))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(Table))

                state.bridge.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getCodeValue(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))
        return listOf(
            { testInstance.getCodeValue("key") },
            { testInstance.getCodeValue(StringValue("key")) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)
                testInstance["key"] = CodeValue("print('doing something interesting')")

                val result = testFn()
                assertThat(result, equalTo(CodeValue("print('doing something interesting')")))
                assertThat(testInstance.length(), equalTo(TableLength(1)))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(Table))

                state.bridge.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getNumberValue(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))
        return listOf(
            { testInstance.getNumberValue("key") },
            { testInstance.getNumberValue(StringValue("key")) },
            { testInstance.getInt("key") },
            { testInstance.getInt(StringValue("key")) },
            { testInstance.getLong("key") },
            { testInstance.getLong(StringValue("key")) },
            { testInstance.getFloat("key") },
            { testInstance.getFloat(StringValue("key")) },
            { testInstance.getDouble("key") },
            { testInstance.getDouble(StringValue("key")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)
                testInstance["key"] = 23

                when (val result = testFn()) {
                    is NumberValue -> assertThat(result, equalTo(NumberValue(23.0)))
                    is Int -> assertThat(result, equalTo(23))
                    is Long -> assertThat(result, equalTo(23L))
                    is Float -> assertThat(result, equalTo(23.0f))
                    is Double -> assertThat(result, equalTo(23.0))
                    else -> TODO()
                }

                assertThat(testInstance.length(), equalTo(TableLength(1)))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(Table))

                state.bridge.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getStringValue(): List<DynamicTest> {
        val testInstance = TableProxy(TableProxyContext(1, state))
        return listOf(
            { testInstance.getString("key") },
            { testInstance.getString(StringValue("key")) },
            { testInstance.getStringValue("key") },
            { testInstance.getStringValue(StringValue("key")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                bridge.tableCreate(0, 0)
                testInstance["key"] = "Hamal Rocks"

                when (val result = testFn()) {
                    is StringValue -> assertThat(result, equalTo(StringValue("Hamal Rocks")))
                    is String -> assertThat(result, equalTo("Hamal Rocks"))
                    else -> TODO()
                }

                assertThat(testInstance.length(), equalTo(TableLength(1)))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
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