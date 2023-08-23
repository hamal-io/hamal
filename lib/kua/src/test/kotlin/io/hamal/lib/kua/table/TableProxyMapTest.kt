package io.hamal.lib.kua.table

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.type.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class TableProxyMapTest {

    @TestFactory
    fun set(): List<DynamicTest> {
        lateinit var testInstance: TableProxyMap
        return listOf(
            { testInstance.set("key", true) },
            { testInstance.set("key", TrueValue) },
            { testInstance.set(StringType("key"), TrueValue) },

            { testInstance.set("key", CodeType("print('hacked')")) },
            { testInstance.set(StringType("key"), CodeType("print('hacked')")) },

            { testInstance.set("key", 23) },
            { testInstance.set("key", 23f) },
            { testInstance.set("key", 23L) },
            { testInstance.set("key", 23.23) },
            { testInstance.set("key", NumberType(23.23)) },
            { testInstance.set(StringType("key"), NumberType(23.23)) },

            { testInstance.set("key", "value") },
            { testInstance.set("key", StringType("value")) },
            { testInstance.set(StringType("key"), StringType("value")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                val result = testFn()
                assertThat("item set", testInstance.length, equalTo(1))
                assertThat(result, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(TableType::class))

                state.native.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun unset(): List<DynamicTest> {
        lateinit var testInstance: TableProxyMap
        return listOf(
            { testInstance.unset("key") },
            { testInstance.unset(StringType("key")) },
            { testInstance.set("key", NilType) },
            { testInstance.set(StringType("key"), NilType) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                testInstance["key"] = "value"
                testInstance["another-key"] = "another-value"
                assertThat(testInstance.length, equalTo(2))

                testFn()
                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(TableType::class))

                state.native.tableGetField(1, "another-key")
                assertThat(state.getString(-1), equalTo("another-value"))

                state.native.pop(2)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getBooleanValue(): List<DynamicTest> {
        lateinit var testInstance: TableProxyMap
        return listOf(
            { testInstance.getBooleanType("key") },
            { testInstance.getBooleanType(StringType("key")) },
            { testInstance.getBoolean("key") },
            { testInstance.getBoolean(StringType("key")) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {

                testInstance = state.tableCreateMap()
                testInstance["key"] = true

                when (val result = testFn()) {
                    is BooleanType -> assertThat(result, equalTo(TrueValue))
                    is Boolean -> assertThat(result, equalTo(true))
                    else -> TODO()
                }

                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(TableType::class))

                state.native.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getCodeValue(): List<DynamicTest> {
        lateinit var testInstance: TableProxyMap
        return listOf(
            { testInstance.getCode("key") },
            { testInstance.getCode(StringType("key")) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()
                testInstance["key"] = "print('doing something interesting')"

                val result = testFn()
                assertThat(result, equalTo(CodeType("print('doing something interesting')")))
                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(TableType::class))

                state.native.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getNumberValue(): List<DynamicTest> {
        lateinit var testInstance: TableProxyMap
        return listOf(
            { testInstance.getNumberType("key") },
            { testInstance.getNumberType(StringType("key")) },
            { testInstance.getInt("key") },
            { testInstance.getInt(StringType("key")) },
            { testInstance.getLong("key") },
            { testInstance.getLong(StringType("key")) },
            { testInstance.getFloat("key") },
            { testInstance.getFloat(StringType("key")) },
            { testInstance.getDouble("key") },
            { testInstance.getDouble(StringType("key")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                testInstance["key"] = 23

                when (val result = testFn()) {
                    is NumberType -> assertThat(result, equalTo(NumberType(23.0)))
                    is Int -> assertThat(result, equalTo(23))
                    is Long -> assertThat(result, equalTo(23L))
                    is Float -> assertThat(result, equalTo(23.0f))
                    is Double -> assertThat(result, equalTo(23.0))
                    else -> TODO()
                }

                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(TableType::class))

                state.native.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getStringValue(): List<DynamicTest> {
        lateinit var testInstance: TableProxyMap
        return listOf(
            { testInstance.getString("key") },
            { testInstance.getString(StringType("key")) },
            { testInstance.getStringType("key") },
            { testInstance.getStringType(StringType("key")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                testInstance["key"] = "Hamal Rocks"

                when (val result = testFn()) {
                    is StringType -> assertThat(result, equalTo(StringType("Hamal Rocks")))
                    is String -> assertThat(result, equalTo("Hamal Rocks"))
                    else -> TODO()
                }

                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(TableType::class))

                state.native.pop(1)
                verifyStackIsEmpty()
            }
        }
    }

    private val state = run {
        NativeLoader.load(Resources)
        ClosableState(Sandbox(NopSandboxContext()).native)
    }

    private fun verifyStackIsEmpty() {
        assertThat("Stack is empty", state.isEmpty(), equalTo(true))
    }
}