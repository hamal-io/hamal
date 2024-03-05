package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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
            KuaTrue to { assertThat(testInstance.getBoolean(1), equalTo(KuaTrue)) },
            KuaFalse to { assertThat(testInstance.getBoolean(1), equalTo(KuaFalse)) },
            KuaDecimal(42.24) to { assertThat(testInstance.getDecimal(1), equalTo(KuaDecimal(42.24))) },
            KuaError("Some Error") to { assertThat(testInstance.getError(1), equalTo(KuaError("Some Error"))) },
            KuaNumber(231123) to { assertThat(testInstance.getNumber(1), equalTo(KuaNumber(231123))) },
            KuaString("Hamal Rocks") to { assertThat(testInstance.getString(1), equalTo(KuaString("Hamal Rocks"))) }
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
    fun `append - nil`() {
        val testInstance = state.tableCreate()
        testInstance.append(KuaNil).also { result ->
            assertThat(result, equalTo(TableLength(1)))
        }
        assertThat(state.topGet(), equalTo(StackTop(1)))

        assertThat(testInstance.getNil(1), equalTo(KuaNil))
        state.topPop(1)
    }

    @Test
    fun `get`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaTrue
        )

        testInstance.get(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaTrue))
        }
    }

    @Test
    fun `get - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaTrue
        )

        testInstance.get(KuaString("anotherKey")).also { result ->
            assertThat(result, equalTo(KuaNil))
        }
    }

    @Test
    fun `getBoolean`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaTrue
        )

        testInstance.getBoolean(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaTrue))
        }
    }

    @Test
    fun `getBoolean - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaTrue
        )

        assertThrows<IllegalStateException> {
            testInstance.getBoolean(KuaString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be boolean but was nil")) }
    }

    @Test
    fun `getBoolean - but different value`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaNumber(23)
        )

        assertThrows<IllegalStateException> {
            testInstance.getBoolean(KuaString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be boolean but was number")) }
    }

    @Test
    fun `getDecimal`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaDecimal(24.42)
        )

        testInstance.getDecimal(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaDecimal(24.42)))
        }
    }

    @Test
    fun `getDecimal - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaDecimal(24.42)
        )

        assertThrows<IllegalStateException> {
            testInstance.getDecimal(KuaString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be decimal but was nil")) }
    }

    @Test
    fun `getDecimal - but different value`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getDecimal(KuaString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be decimal but was string")) }
    }

    @Test
    fun `getError`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaError("Some expected Error")
        )

        testInstance.getError(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaError("Some expected Error")))
        }
    }

    @Test
    fun `getError - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaError("Some expected Error")
        )

        assertThrows<IllegalStateException> {
            testInstance.getError(KuaString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be error but was nil")) }
    }

    @Test
    fun `getError - but different value`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getError(KuaString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be error but was string")) }
    }

    @Test
    fun `getNumber`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaNumber(24.42)
        )

        testInstance.getNumber(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaNumber(24.42)))
        }
    }

    @Test
    fun `getNumber - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaNumber(24.42)
        )

        assertThrows<IllegalStateException> {
            testInstance.getNumber(KuaString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be number but was nil")) }
    }

    @Test
    fun `getNumber - but different value`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getNumber(KuaString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be number but was string")) }
    }

    @Test
    fun `getString`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaString("hamal")
        )

        testInstance.getString(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaString("hamal")))
        }
    }

    @Test
    fun `getString - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaString("hamal")
        )

        assertThrows<IllegalStateException> {
            testInstance.getString(KuaString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be string but was nil")) }
    }

    @Test
    fun `getString - but different value`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaNumber(404)
        )

        assertThrows<IllegalStateException> {
            testInstance.getString(KuaString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be string but was number")) }
    }

    @Test
    fun `getTable`() {
        val testInstance = state.tableCreate(
            KuaString("key") to state.tableCreate(
                KuaString("answer") to KuaNumber(42)
            )
        )

        testInstance.getTable(KuaString("key")).also { result ->
            assertThat(result.length, equalTo(TableLength(1)))
            assertThat(result.getNumber("answer"), equalTo(KuaNumber(42)))
        }
    }

    @Test
    fun `getTable - but value not found`() {
        val testInstance = state.tableCreate(
            KuaString("key") to state.tableCreate(
                KuaString("answer") to KuaNumber(42)
            )
        )

        assertThrows<IllegalStateException> {
            testInstance.getTable(KuaString("anotherKey"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was nil")) }
    }

    @Test
    fun `getTable - but different value`() {
        val testInstance = state.tableCreate(
            KuaString("key") to KuaString("Hamal Rocks")
        )

        assertThrows<IllegalStateException> {
            testInstance.getTable(KuaString("key"))
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was string")) }
    }


    @TestFactory
    fun `set - kua values`(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            KuaTrue,
            KuaFalse,
            KuaDecimal("123"),
            KuaNumber(42.24),
            KuaError("Some Error Message"),
            KuaString("Hamal Rocks"),
        ).map { value ->
            dynamicTest(value.toString()) {
                testInstance = state.tableCreate()

                testInstance.set("key", value).also { tableLength ->
                    assertThat(tableLength, equalTo(TableLength(1)))
                }

                testInstance.get(KuaString("key")).also { result ->
                    assertThat(result, equalTo(value))
                }

                assertThat("Two elements on stack", state.topGet(), equalTo(StackTop(2)))
                assertThat("Table on the bottom of the stack", state.type(1), equalTo(KuaTable::class))

                state.topPop(2)
            }
        }
    }

    @Test
    fun `set - nil`() {
        val testInstance = state.tableCreate()

        testInstance.set("key", KuaNil).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(0)))
        }

        testInstance.get(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaNil))
        }

        assertThat("Two elements on stack", state.topGet(), equalTo(StackTop(2)))
        assertThat("Table on bottom of the stack", state.type(1), equalTo(KuaTable::class))
        assertThat("Nil on top of the stack", state.type(2), equalTo(KuaNil::class))

        state.topPop(2)
    }


    @Test
    fun `set - table`() {
        val testTable = state.tableCreate(
            KuaString("hamal") to KuaString("rocks")
        )


        val testInstance = state.tableCreate()
        testInstance.set("key", testTable).also { tableLength ->
            assertThat(tableLength, equalTo(TableLength(1)))
        }

        testInstance.get(KuaString("key")).also { result ->
            require(result is KuaTable)
            assertThat(result.getString("hamal"), equalTo(KuaString("rocks")))

        }

        state.topPop(3)
    }

    @Test
    fun `unset`() {
        val testInstance = state.tableCreate()

        testInstance["key"] = KuaNumber(42)

        testInstance.unset("key").also { tableLength -> assertThat(tableLength, equalTo(TableLength(0))) }
        testInstance.get(KuaString("key")).also { result ->
            assertThat(result, equalTo(KuaNil))
        }

        assertThat("Two elements on stack", state.topGet(), equalTo(StackTop(2)))
        assertThat("Table on bottom of the stack", state.type(1), equalTo(KuaTable::class))
        assertThat("Nil on top of the stack", state.type(2), equalTo(KuaNil::class))

        state.topPop(2)
    }


    @Test
    fun `unset - but key does not exists`() {
        val testInstance = state.tableCreate()

        testInstance["key"] = KuaNumber(42)

        testInstance.unset("anotherKey").also { tableLength -> assertThat(tableLength, equalTo(TableLength(1))) }
        testInstance.get(KuaString("key")).also { result -> assertThat(result, equalTo(KuaNumber(42))) }

        state.topPop(2)
    }


    private val state = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }

}