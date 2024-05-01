package io.hamal.lib.kua.value

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.value.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class KuaTableTest {

    @TestFactory
    fun append(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            ValueTrue to { assertThat(testInstance.getBoolean(1), equalTo(ValueTrue)) },
            ValueFalse to { assertThat(testInstance.getBoolean(1), equalTo(ValueFalse)) },
            ValueDecimal(42.24) to { assertThat(testInstance.getDecimal(1), equalTo(ValueDecimal(42.24))) },
            ValueError("Some Error") to { assertThat(testInstance.getError(1), equalTo(ValueError("Some Error"))) },
            ValueNumber(231123) to { assertThat(testInstance.getNumber(1), equalTo(ValueNumber(231123))) },
            ValueString("Hamal Rocks") to { assertThat(testInstance.getString(1), equalTo(ValueString("Hamal Rocks"))) }
        ).map { (value, verify) ->
            dynamicTest(value.toString()) {
                testInstance = state.tableCreate()
                testInstance.append(value).also { result -> assertThat(result, equalTo(TableLength(1))) }
                assertThat(state.topGet(), equalTo(StackTop(1)))
                verify()
                state.topPop(2)
            }
        }
    }

    @Test
    fun `append - table`() {
        val testInstance = state.tableCreate()
        val tableToAppend = state.tableCreate()
        testInstance.append(tableToAppend).also { result ->
            assertThat(result, equalTo(TableLength(1)))
        }
        assertThat(state.topGet(), equalTo(StackTop(2)))
        assertThat(state.type(-1), equalTo(KuaTable::class))
        state.topPop(2)
    }

    @Test
    fun `append - function`() {
        val testInstance = state.tableCreate()
        val testFunction = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                TODO("Not yet implemented")
            }
        }

        testInstance.append(testFunction).also { result ->
            assertThat(result, equalTo(TableLength(1)))
        }
        assertThat(state.topGet(), equalTo(StackTop(1)))

        state.tableRawGetIdx(1, 1)
        assertThat(state.type(-1), equalTo(KuaFunction::class))
        state.topPop(2)
    }


    @Test
    fun `append - nil`() {
        val testInstance = state.tableCreate()
        testInstance.append(ValueNil).also { result ->
            assertThat(result, equalTo(TableLength(1)))
        }
        assertThat(state.topGet(), equalTo(StackTop(1)))

        assertThat(testInstance.getNil(1), equalTo(ValueNil))
        state.topPop(1)
    }

    @Test
    fun `get`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueTrue
        )

        testInstance.get(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueTrue))
        }
    }

    @Test
    fun `get - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueTrue
        )

        testInstance.get(ValueString("anotherKey")).also { result ->
            assertThat(result, equalTo(ValueNil))
        }
    }

    @Test
    fun `findBoolean`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueTrue
        )

        testInstance.findBoolean(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueTrue))
        }
    }

    @Test
    fun `findBoolean - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueTrue
        )

        assertThat(testInstance.findBoolean(ValueString("anotherKey")), nullValue())
    }


    @Test
    fun `getBoolean`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueTrue
        )

        testInstance.getBoolean(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueTrue))
        }
    }

    @Test
    fun `getBoolean - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueTrue
        )

        assertThrows<NoSuchElementException> {
            testInstance.getBoolean(ValueString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("anotherKey not found")) }
    }

    @Test
    fun `getBoolean - but different value`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueNumber(23)
        )

        assertThrows<IllegalStateException> {
            testInstance.getBoolean(ValueString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be boolean but was number")) }
    }

    @Test
    fun `findDecimal`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueDecimal(24.42)
        )

        testInstance.findDecimal(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueDecimal(24.42)))
        }
    }

    @Test
    fun `findDecimal - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueDecimal(24.42)
        )

        assertThat(testInstance.findDecimal(ValueString("anotherKey")), nullValue())
    }

    @Test
    fun `getDecimal`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueDecimal(24.42)
        )

        testInstance.getDecimal(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueDecimal(24.42)))
        }
    }

    @Test
    fun `getDecimal - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueDecimal(24.42)
        )

        assertThrows<NoSuchElementException> {
            testInstance.getDecimal(ValueString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("anotherKey not found")) }
    }

    @Test
    fun `getDecimal - but different value`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getDecimal(ValueString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be decimal but was string")) }
    }

    @Test
    fun `findError`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueError("Some expected Error")
        )

        testInstance.findError(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueError("Some expected Error")))
        }
    }

    @Test
    fun `findError - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueError("Some expected Error")
        )

        assertThat(testInstance.findError(ValueString("anotherKey")), nullValue())
    }


    @Test
    fun `getError`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueError("Some expected Error")
        )

        testInstance.getError(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueError("Some expected Error")))
        }
    }

    @Test
    fun `getError - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueError("Some expected Error")
        )

        assertThrows<NoSuchElementException> {
            testInstance.getError(ValueString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("anotherKey not found")) }
    }

    @Test
    fun `getError - but different value`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getError(ValueString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be error but was string")) }
    }

    @Test
    fun `findNumber`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueNumber(24.42)
        )

        testInstance.findNumber(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueNumber(24.42)))
        }
    }

    @Test
    fun `findNumber - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueNumber(24.42)
        )

        assertThat(testInstance.findNumber(ValueString("anotherKey")), nullValue())
    }

    @Test
    fun `getNumber`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueNumber(24.42)
        )

        testInstance.getNumber(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueNumber(24.42)))
        }
    }

    @Test
    fun `getNumber - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueNumber(24.42)
        )

        assertThrows<NoSuchElementException> {
            testInstance.getNumber(ValueString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("anotherKey not found")) }
    }

    @Test
    fun `getNumber - but different value`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getNumber(ValueString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be number but was string")) }
    }

    @Test
    fun `findString`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("hamal")
        )

        testInstance.findString(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueString("hamal")))
        }
    }

    @Test
    fun `findString - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("hamal")
        )
        assertThat(testInstance.findString(ValueString("anotherKey")), nullValue())
    }

    @Test
    fun `getString`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("hamal")
        )

        testInstance.getString(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueString("hamal")))
        }
    }

    @Test
    fun `getString - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("hamal")
        )

        assertThrows<NoSuchElementException> {
            testInstance.getString(ValueString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("anotherKey not found")) }
    }

    @Test
    fun `getString - but different value`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueNumber(404)
        )

        assertThrows<IllegalStateException> {
            testInstance.getString(ValueString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be string but was number")) }
    }

    @Test
    fun `findTable`() {
        val testInstance = state.tableCreate(
            ValueString("key") to state.tableCreate(
                ValueString("answer") to ValueNumber(42)
            )
        )

        testInstance.findTable(ValueString("key")).also { result ->
            assertThat(result?.length, equalTo(TableLength(1)))
            assertThat(result?.getNumber("answer"), equalTo(ValueNumber(42)))
        }
    }

    @Test
    fun `findTable - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to state.tableCreate(
                ValueString("answer") to ValueNumber(42)
            )
        )

        assertThat(testInstance.findString(ValueString("anotherKey")), nullValue())
    }


    @Test
    fun `getTable`() {
        val testInstance = state.tableCreate(
            ValueString("key") to state.tableCreate(
                ValueString("answer") to ValueNumber(42)
            )
        )

        testInstance.getTable(ValueString("key")).also { result ->
            assertThat(result.length, equalTo(TableLength(1)))
            assertThat(result.getNumber("answer"), equalTo(ValueNumber(42)))
        }
    }

    @Test
    fun `getTable - but value not found`() {
        val testInstance = state.tableCreate(
            ValueString("key") to state.tableCreate(
                ValueString("answer") to ValueNumber(42)
            )
        )

        assertThrows<NoSuchElementException> {
            testInstance.getString(ValueString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("anotherKey not found")) }
    }

    @Test
    fun `getTable - but different value`() {
        val testInstance = state.tableCreate(
            ValueString("key") to ValueString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getTable(ValueString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was string")) }
    }


    @TestFactory
    fun `set`(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            ValueTrue,
            ValueFalse,
            ValueDecimal("123"),
            ValueNumber(42.24),
            ValueError("Some Error Message"),
            ValueString("Hamal Rocks"),
        ).map { value ->
            dynamicTest(value.toString()) {
                testInstance = state.tableCreate()

                testInstance.set("key", value).also { tableLength ->
                    assertThat(tableLength, equalTo(TableLength(1)))
                }

                testInstance.get(ValueString("key")).also { result ->
                    assertThat(result, equalTo(value))
                }

                assertThat("Two elements on stack", state.topGet(), equalTo(StackTop(2)))
                assertThat("Table on the bottom of the stack", state.type(1), equalTo(KuaTable::class))

                state.topPop(2)
            }
        }
    }

    @Test
    fun `set - function`() {
        val testInstance = state.tableCreate()
        val testFunction = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                TODO("Not yet implemented")
            }
        }

        testInstance.set("key", testFunction).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        assertThat(state.topGet(), equalTo(StackTop(1)))

        state.tableFieldGet(1, ValueString("key"))
        assertThat(state.type(-1), equalTo(KuaFunction::class))
        state.topPop(2)
    }

    @Test
    fun `set - nil`() {
        val testInstance = state.tableCreate()

        testInstance.set("key", ValueNil).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(0)))
        }

        testInstance.get(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueNil))
        }

        assertThat("Two elements on stack", state.topGet(), equalTo(StackTop(2)))
        assertThat("Table on bottom of the stack", state.type(1), equalTo(KuaTable::class))
        assertThat("Nil on top of the stack", state.type(2), equalTo(ValueNil::class))

        state.topPop(2)
    }

    @Test
    fun `set - table`() {
        val testTable = state.tableCreate(
            ValueString("hamal") to ValueString("rocks")
        )


        val testInstance = state.tableCreate()
        testInstance.set("key", testTable).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        testInstance.get(ValueString("key")).also { result ->
            require(result is KuaTable)
            assertThat(result.getString("hamal"), equalTo(ValueString("rocks")))

        }

        state.topPop(3)
    }

    @Test
    fun `set - nested table`() {
        val testInstance = state.tableCreate()
        testInstance["headers"] = state.tableCreate(
            ValueString("deeper") to state.tableCreate(
                ValueString("Answer") to ValueString("42")
            )
        )

        testInstance.getTable("headers").getTable("deeper").getString("Answer").also { result ->
            assertThat(result, equalTo(ValueString("42")))
        }
    }

    @Test
    fun `unset`() {
        val testInstance = state.tableCreate()

        testInstance["key"] = ValueNumber(42)

        testInstance.unset("key").also { tableLength -> assertThat(tableLength, equalTo(TableLength(0))) }
        testInstance.get(ValueString("key")).also { result ->
            assertThat(result, equalTo(ValueNil))
        }

        assertThat("Two elements on stack", state.topGet(), equalTo(StackTop(2)))
        assertThat("Table on bottom of the stack", state.type(1), equalTo(KuaTable::class))
        assertThat("Nil on top of the stack", state.type(2), equalTo(ValueNil::class))

        state.topPop(2)
    }


    @Test
    fun `unset - but key does not exists`() {
        val testInstance = state.tableCreate()

        testInstance["key"] = ValueNumber(42)

        testInstance.unset("anotherKey").also { tableLength -> assertThat(tableLength, equalTo(TableLength(1))) }
        testInstance.get(ValueString("key")).also { result -> assertThat(result, equalTo(ValueNumber(42))) }

        state.topPop(2)
    }


    private val state = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }

}