package io.hamal.lib.kua

import io.hamal.lib.kua.value.Function2In2Out
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.function.FunctionContext
import io.hamal.lib.kua.value.function.FunctionInput2Schema
import io.hamal.lib.kua.value.function.FunctionOutput2Schema
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


@DisplayName("luaVersionNumber()")
internal class LuaVersionNumberTest : BaseStateTest() {
    @Test
    fun `Loads current lua version number`() {
        val result = testInstance.luaVersionNumber()
        assertThat("5.4", result, equalTo(504))
    }
}

@DisplayName("luaIntegerWidth()")
internal class LuaIntegerWidthTest : BaseStateTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.luaIntegerWidth()
        assertThat("64bit platform - 8 byte", result, equalTo(8))
    }
}

@DisplayName("luaRegistryIndex()")
internal class LuaRegistryIndexTest : BaseStateTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.luaRegistryIndex()
        assertThat(result, equalTo(-1001000))
    }
}

@DisplayName("top()")
internal class TopTest : BaseStateTest() {
    @Test
    fun `Nothing pushed on the stack`() {
        val result = testInstance.top()
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Pushing to the stack cause stack to grow`() {
        repeat(100) { idx ->
            assertThat(testInstance.top(), equalTo(idx))
            testInstance.pushBoolean(true)
        }
    }
}

@DisplayName("type()")
internal class TypeTest : BaseStateTest() {

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.type(0)
        }
        assertThat(exception.message, equalTo("Index must not be 0"))
    }

    @Test
    fun `Tries to get type with index bigger than stack size`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.type(2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to get type with abs(negative index) bigger than stack size`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.type(-2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Nil`() {
        testInstance.pushNil()
        val result = testInstance.type(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Boolean`() {
        testInstance.pushBoolean(true)
        val result = testInstance.type(1)
        assertThat(result, equalTo(1))
    }

    @Test
    @Disabled
    fun `LightUserData`() {
        TODO()
    }

    @Test
    fun `Number`() {
        testInstance.pushNumber(13.0)
        val result = testInstance.type(1)
        assertThat(result, equalTo(3))
    }

    @Test
    fun `String`() {
        testInstance.pushString("hamal")
        val result = testInstance.type(1)
        assertThat(result, equalTo(4))
    }

    @Test
    fun `Table`() {
        testInstance.createTable(0, 0)
        val result = testInstance.type(1)
        assertThat(result, equalTo(5))
    }

    @Test
    fun `Function`() {
        testInstance.loadString("local x = 10")
        assertThat(testInstance.type(1), equalTo(6))
    }

    @Test
    @Disabled
    fun `UserData`() {
        TODO()
    }

    @Test
    @Disabled
    fun `Thread`() {
        TODO()
    }
}

@DisplayName("setGlobal()")
internal class SetGlobalTest : BaseStateTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("getGlobal()")
internal class GetGlobalTest : BaseStateTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("push()")
internal class PushTest : BaseStateTest() {
    @Test
    fun `Tries to push with negative idx bigger than stack size`() {
        repeat(999999) { testInstance.pushNil() }
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.push(-1)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }

    @Test
    fun `Pushes value at the top of the stack`() {
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(2.0)
        testInstance.pushNumber(3.0)

        val result = testInstance.push(1)
        assertThat(result, equalTo(4))
        assertThat(testInstance.top(), equalTo(4))

        assertThat(testInstance.toNumber(4), equalTo(1.0))
    }

    @Test
    fun `Pushes value at the top of the stack with negative index`() {
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(2.0)
        testInstance.pushNumber(3.0)

        val result = testInstance.push(-1)
        assertThat(result, equalTo(4))
        assertThat(testInstance.top(), equalTo(4))

        assertThat(testInstance.toNumber(4), equalTo(3.0))
    }

    @Test
    fun `Tries to push index 0 `() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.push(0)
        }
        assertThat(exception.message, equalTo("Index must not be 0"))
    }

    @Test
    fun `Tries to push with index bigger than stack size`() {
        repeat(999999) { testInstance.pushNil() }
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.push(2)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("pushBoolean()")
internal class PushBooleanTest : BaseStateTest() {
    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushBoolean(true)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toBoolean(1), equalTo(true))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushBoolean(true)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("pushFuncValue()")
internal class PushFuncValueTest : BaseStateTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("pushNil()")
internal class PushNilTest : BaseStateTest() {

    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushNil()
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushNil() }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushNil()
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("pushNumber()")
internal class PushNumberTest : BaseStateTest() {

    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushNumber(13.37)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toNumber(1), equalTo(13.37))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushNumber(it.toDouble()) }
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushNumber(-1.0)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("pushString()")
internal class PushStringTest : BaseStateTest() {
    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushString("hamal")
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toString(1), equalTo("hamal"))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushString("code-sleep-repeat") }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushString("until you can not anymore")
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("pop()")
internal class PopTest : BaseStateTest() {
    @Test
    fun `Tries to pop negative amount from empty`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pop(-1)
        }
        assertThat(exception.message, equalTo("Total must be positive (>0)"))
    }

    @Test
    fun `Tries to pop -1 elements from empty stack`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pop(0)
        }
        assertThat(exception.message, equalTo("Total must be positive (>0)"))
    }

    @Test
    fun `Tries to pop 1 element from empty stack`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pop(1)
        }
        assertThat(exception.message, equalTo("Prevented stack underflow"))
    }

    @Test
    fun `Pops 2 elements from stack`() {
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(2.0)
        testInstance.pushNumber(3.0)

        val result = testInstance.pop(2)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))

        assertThat(testInstance.toNumber(1), equalTo(1.0))
    }
}

@DisplayName("toBoolean()")
internal class ToBooleanTest : BaseStateTest() {

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(0)
        }
        assertThat(exception.message, equalTo("Index must not be 0"))
    }

    @Test
    fun `Tries to read boolean with index bigger than stack size`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read boolean with abs(negative index) bigger than stack size`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(-2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read number as boolean`() {
        testInstance.pushNumber(1.0)
        val exception = assertThrows<IllegalStateException> {
            testInstance.toBoolean(1)
        }
        assertThat(exception.message, equalTo("Expected type to be boolean but was number"))
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushBoolean(true)
        assertThat(testInstance.toBoolean(1), equalTo(true))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushBoolean(false)
        assertThat(testInstance.toBoolean(2), equalTo(false))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushBoolean(true)
        assertThat(testInstance.toBoolean(-1), equalTo(true))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushBoolean(false)
        assertThat(testInstance.toBoolean(-1), equalTo(false))
        assertThat(testInstance.top(), equalTo(2))
    }
}

@DisplayName("toNumber()")
internal class ToNumberTest : BaseStateTest() {
    @Test
    fun `Tries to read Number with 0 index`() {
        testInstance.pushNumber(812.123)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toNumber(0)
        }
        assertThat(exception.message, equalTo("Index must not be 0"))
    }

    @Test
    fun `Tries to read Number with index bigger than stack size`() {
        testInstance.pushNumber(123.321)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toNumber(2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read Number with abs(negative index) bigger than stack size`() {
        testInstance.pushNumber(123.321)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toNumber(-2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }


    @Test
    fun `Tries to read number as Number`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalStateException> {
            testInstance.toNumber(1)
        }
        assertThat(exception.message, equalTo("Expected type to be number but was boolean"))
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushNumber(99.88)
        assertThat(testInstance.toNumber(1), equalTo(99.88))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(88.77)
        assertThat(testInstance.toNumber(2), equalTo(88.77))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushNumber(99.88)
        assertThat(testInstance.toNumber(-1), equalTo(99.88))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(88.77)
        assertThat(testInstance.toNumber(-1), equalTo(88.77))
        assertThat(testInstance.top(), equalTo(2))
    }
}

@DisplayName("toString()")
internal class ToStringTest : BaseStateTest() {

    @Test
    fun `Tries to read String with 0 index`() {
        testInstance.pushString("some-string")
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toString(0)
        }
        assertThat(exception.message, equalTo("Index must not be 0"))
    }

    @Test
    fun `Tries to read String with index bigger than stack size`() {
        testInstance.pushString("some-string")
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toString(2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read String with abs(negative index) bigger than stack size`() {
        testInstance.pushString("some-string")
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toString(2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read number as String`() {
        testInstance.pushNumber(1.0)
        val exception = assertThrows<IllegalStateException> {
            testInstance.toString(1)
        }
        assertThat(exception.message, equalTo("Expected type to be string but was number"))
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushString("eat-poop-sleep-repeat")
        assertThat(testInstance.toString(1), equalTo("eat-poop-sleep-repeat"))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("or-write-some-code")
        assertThat(testInstance.toString(2), equalTo("or-write-some-code"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushString("eat-poop-sleep-repeat")
        assertThat(testInstance.toString(-1), equalTo("eat-poop-sleep-repeat"))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("or-write-some-code")
        assertThat(testInstance.toString(-1), equalTo("or-write-some-code"))
        assertThat(testInstance.top(), equalTo(2))
    }
}

@DisplayName("createTableTest()")
internal class CreateTableTest : BaseStateTest() {
    @Test
    fun `Creates an empty table on empty stack`() {
        val result = testInstance.createTable(1, 2)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))

        assertThat(testInstance.type(1), equalTo(5))
    }

    @Test
    fun `Array count must not be negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.createTable(-1, 0)
        }
        assertThat(exception.message, equalTo("Array count must not be negative"))
    }

    @Test
    fun `Records count must not be negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.createTable(0, -1)
        }
        assertThat(exception.message, equalTo("Records count must not be negative"))
    }
}

@DisplayName("setTableField()")
internal class SetTableFieldTest : BaseStateTest() {
    @Test
    fun `Sets value to empty table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        testInstance.getTableField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.getTableLength(1), equalTo(1))
    }


    @Test
    fun `Sets different value for same key table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")

        testInstance.pushNumber(42.0)
        testInstance.setTableField(1, "key")

        testInstance.getTableField(1, "key")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.getTableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")

        testInstance.pushNumber(42.0)
        testInstance.setTableField(1, "different")

        testInstance.getTableField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.getTableField(1, "different")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.getTableLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value from to not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.setTableField(1, "key")
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("getTableField()")
internal class GetTableFieldTest : BaseStateTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        testInstance.getTableField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")

        testInstance.getTableField(1, "does-not-find-anything")
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.getTableField(1, "key")
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")

        repeat(999998) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.getTableField(1, "key")
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("getTableLength()")
internal class GetTableLengthTest : BaseStateTest() {
    @Test
    fun `Size of empty table`() {
        testInstance.createTable(12, 12)
        val result = testInstance.getTableLength(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Size of table with single field`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        val result = testInstance.getTableLength(1)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Size of table with multiple fields`() {
        testInstance.createTable(0, 1)
        repeat(10) { idx ->
            testInstance.pushString("value")
            testInstance.setTableField(1, "key-${idx}")

            val result = testInstance.getTableLength(1)
            assertThat(result, equalTo(idx + 1))
        }
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Does not alter the stack`() {
        testInstance.createTable(0, 1)
        repeat(10) { idx ->
            testInstance.pushString("value")
            testInstance.setTableField(1, "key-${idx}")
            testInstance.getTableField(1, "Key-${idx}")
        }

        assertThat(testInstance.top(), equalTo(11))

        val result = testInstance.getTableLength(1)
        assertThat(result, equalTo(10))

        assertThat(testInstance.top(), equalTo(11))
    }

    @Test
    fun `Tries to get table size from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.getTableLength(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("setTableRaw()")
internal class SetTableRawTest : BaseStateTest() {
    @Test
    fun `Sets value to empty table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.setTableRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.getTableRaw(1)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.getTableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same key`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.setTableRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.pushNumber(42.0)
        testInstance.setTableRaw(1)

        testInstance.getTableField(1, "key")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.getTableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.createTable(0, 1)

        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.setTableRaw(1)

        testInstance.pushString("different")
        testInstance.pushNumber(42.0)
        testInstance.setTableRaw(1)

        testInstance.getTableField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.getTableField(1, "different")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.getTableLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value from to not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.pushString("key")
            testInstance.pushString("value")
            testInstance.setTableRaw(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("setTableRawIdx()")
internal class SetTableRawIdxTest : BaseStateTest() {
    @Test
    fun `Sets value to empty table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableRawIdx(1, 23)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(23.0)
        testInstance.getTableRaw(1)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.getTableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same index`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableRawIdx(1, 23)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(42.0)
        testInstance.setTableRawIdx(1, 23)

        testInstance.pushNumber(23.0)
        testInstance.getTableRaw(1)
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.getTableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.createTable(0, 1)

        testInstance.pushString("value")
        testInstance.setTableRawIdx(1, 2)

        testInstance.pushNumber(42.0)
        testInstance.setTableRawIdx(1, 4)

        testInstance.getTableRawIdx(1, 2)
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.getTableRawIdx(1, 4)
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.getTableLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value from to not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.pushString("value")
            testInstance.setTableRawIdx(1, 4)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("getTableRaw()")
internal class GetTableRawTest : BaseStateTest() {
    @Test
    fun `Gets value from table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.setTableRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.getTableRaw(1)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.setTableRaw(1)

        testInstance.pushString("does-not-find-anything")
        testInstance.getTableRaw(1)
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.pushString("key")
            testInstance.getTableRaw(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.setTableRaw(1)

        repeat(999997) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushString("key")
            testInstance.getTableRaw(1)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("getTableRawIdx()")
internal class GetTableRawIdxTest : BaseStateTest() {
    @Test
    fun `Gets value from table`() {
        testInstance.createTable(0, 1)
        testInstance.pushNumber(5.0)
        testInstance.pushString("value")
        testInstance.setTableRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.getTableRawIdx(1, 5)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.createTable(0, 1)
        testInstance.pushNumber(43.0)
        testInstance.pushString("value")
        testInstance.setTableRaw(1)

        testInstance.getTableRawIdx(1, 1337)
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.getTableRawIdx(1, 3)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableRawIdx(1, 1)

        repeat(999998) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.getTableRawIdx(1, 1)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("getSubTable")
internal class GetSubTableTest : BaseStateTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("loadString()")
internal class LoadStringTest : BaseStateTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("call()")
internal class CallTest : BaseStateTest() {
    @Test
    fun `Calls kotlin function with 2 parameter and 2 receives 2 values back`() {
        testInstance.pushFunctionValue(Magic())
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(5.0)
        testInstance.call(2, 2)

        assertThat(testInstance.toNumber(-1), equalTo(2.0))
        assertThat(testInstance.toNumber(-2), equalTo(20.0))

        testInstance.pop(2)
        verifyStackIsEmpty()
    }

    private class Magic : Function2In2Out<NumberValue, NumberValue, NumberValue, NumberValue>(
        FunctionInput2Schema(NumberValue::class, NumberValue::class),
        FunctionOutput2Schema(NumberValue::class, NumberValue::class)
    ) {

        override fun invoke(
            ctx: FunctionContext,
            arg1: NumberValue,
            arg2: NumberValue
        ): Pair<NumberValue, NumberValue> {
            return Pair(arg2 * 4, arg1 * 2)
        }
    }
}

internal sealed class BaseStateTest {
    val testInstance: Bridge = run {
        ResourceLoader.load()
        Bridge()
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.top(), equalTo(0))
    }
}
