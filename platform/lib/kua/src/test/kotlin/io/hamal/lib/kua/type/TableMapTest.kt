package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class KuaTableMapTest {

    @TestFactory
    fun set(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.set("key", true) },
            { testInstance.set("key", KuaTrue) },
            { testInstance.set(KuaString("key"), KuaTrue) },

            { testInstance.set("key", KuaCode("print('hacked')")) },
            { testInstance.set(KuaString("key"), KuaCode("print('hacked')")) },

            { testInstance.set("key", 23) },
            { testInstance.set("key", 23f) },
            { testInstance.set("key", 23L) },
            { testInstance.set("key", 23.23) },
            { testInstance.set("key", KuaNumber(23.23)) },
            { testInstance.set(KuaString("key"), KuaNumber(23.23)) },

            { testInstance.set("key", "value") },
            { testInstance.set("key", KuaString("value")) },
            { testInstance.set(KuaString("key"), KuaString("value")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                val result = testFn()
                assertThat("item set", testInstance.length, equalTo(1))
                assertThat(result, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(KuaTable::class))

                state.native.topPop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun unset(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.unset("key") },
            { testInstance.unset(KuaString("key")) },
            { testInstance.set("key", KuaNil) },
            { testInstance.set(KuaString("key"), KuaNil) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                testInstance["key"] = "value"
                testInstance["another-key"] = "another-value"
                assertThat(testInstance.length, equalTo(2))

                testFn()
                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(KuaTable::class))

                state.native.tableGetField(1, "another-key")
                assertThat(state.getString(-1), equalTo("another-value"))

                state.native.topPop(2)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getBooleanValue(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.getBooleanType("key") },
            { testInstance.getBooleanType(KuaString("key")) },
            { testInstance.getBoolean("key") },
            { testInstance.getBoolean(KuaString("key")) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {

                testInstance = state.tableCreateMap()
                testInstance["key"] = true

                when (val result = testFn()) {
                    is KuaBoolean -> assertThat(result, equalTo(KuaTrue))
                    is Boolean -> assertThat(result, equalTo(true))
                    else -> TODO()
                }

                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(KuaTable::class))

                state.native.topPop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getCodeValue(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.getCode("key") },
            { testInstance.getCode(KuaString("key")) },
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()
                testInstance["key"] = "print('doing something interesting')"

                val result = testFn()
                assertThat(result, equalTo(KuaCode("print('doing something interesting')")))
                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(KuaTable::class))

                state.native.topPop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getNumberValue(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.getNumberType("key") },
            { testInstance.getNumberType(KuaString("key")) },
            { testInstance.getInt("key") },
            { testInstance.getInt(KuaString("key")) },
            { testInstance.getLong("key") },
            { testInstance.getLong(KuaString("key")) },
            { testInstance.getFloat("key") },
            { testInstance.getFloat(KuaString("key")) },
            { testInstance.getDouble("key") },
            { testInstance.getDouble(KuaString("key")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                testInstance["key"] = 23

                when (val result = testFn()) {
                    is KuaNumber -> assertThat(result, equalTo(KuaNumber(23.0)))
                    is Int -> assertThat(result, equalTo(23))
                    is Long -> assertThat(result, equalTo(23L))
                    is Float -> assertThat(result, equalTo(23.0f))
                    is Double -> assertThat(result, equalTo(23.0))
                    else -> TODO()
                }

                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(KuaTable::class))

                state.native.topPop(1)
                verifyStackIsEmpty()
            }
        }
    }

    @TestFactory
    fun getStringValue(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.getString("key") },
            { testInstance.getString(KuaString("key")) },
            { testInstance.getStringType("key") },
            { testInstance.getStringType(KuaString("key")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateMap()

                testInstance["key"] = "Hamal Rocks"

                when (val result = testFn()) {
                    is KuaString -> assertThat(result, equalTo(KuaString("Hamal Rocks")))
                    is String -> assertThat(result, equalTo("Hamal Rocks"))
                    else -> TODO()
                }

                assertThat(testInstance.length, equalTo(1))

                assertThat("One element on stack", state.top, equalTo(StackTop(1)))
                assertThat("Only table on stack", state.type(1), equalTo(KuaTable::class))

                state.native.topPop(1)
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