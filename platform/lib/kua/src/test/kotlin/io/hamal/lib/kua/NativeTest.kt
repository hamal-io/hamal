package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.DecimalType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.NumberType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("luaVersionNumber()")
internal class LuaVersionNumberTest : NativeTest() {
    @Test
    fun `Loads current lua version number`() {
        val result = testInstance.luaVersionNumber()
        assertThat("5.4", result, equalTo(504))
    }
}

@DisplayName("luaIntegerWidth()")
internal class LuaIntegerWidthTest : NativeTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.luaIntegerWidth()
        assertThat("64bit platform - 8 byte", result, equalTo(8))
    }
}

@DisplayName("luaRegistryIndex()")
internal class LuaRegistryIndexTest : NativeTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.luaRegistryIndex()
        assertThat(result, equalTo(-1001000))
    }
}

@DisplayName("top()")
internal class TopTest : NativeTest() {
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

@DisplayName("setTop()")
internal class SetTopTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("absIndex()")
internal class AbsIndexTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("pushTop()")
internal class PushTopTest : NativeTest() {

    @Test
    fun `Pushes value at the top of stack which is already at the top`() {
        testInstance.pushBoolean(true)
        testInstance.pushBoolean(false)
        testInstance.pushNumber(13.37)
        testInstance.pushTop(3)

        assertThat(testInstance.type(3), equalTo(3))
        assertThat(testInstance.type(4), equalTo(3))
        assertThat(testInstance.toNumber(3), equalTo(13.37))
        assertThat(testInstance.toNumber(4), equalTo(13.37))
        assertThat(testInstance.top(), equalTo(4))
    }

    @Test
    fun `Pushes table at the top of the stack`() {
        testInstance.tableCreate(0, 0)
        testInstance.pushNil()
        testInstance.pushBoolean(true)
        testInstance.pushNumber(42.0)
        testInstance.pushString("Hamal Rocks")
        assertThat(testInstance.type(-1), equalTo(4))

        testInstance.pushTop(1)
        assertThat(testInstance.type(1), equalTo(5))
        assertThat(testInstance.type(-1), equalTo(5))
        assertThat(testInstance.top(), equalTo(6))
    }

    @Test
    fun `Pushing value outside of stack causes pushing nil`() {
        verifyStackIsEmpty()

        val result = testInstance.pushTop(1337)
        assertThat(result, equalTo(1))

        assertThat(testInstance.type(-1), equalTo(0))

        val secondResult = testInstance.pushTop(23)
        assertThat(secondResult, equalTo(2))

        assertThat(testInstance.type(-1), equalTo(0))
        assertThat(testInstance.type(-2), equalTo(0))

        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        testInstance.pushBoolean(true)
        repeat(999_998) { idx ->
            val result = testInstance.pushTop(1)
            assertThat(result, equalTo(idx + 2))
        }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushTop(1)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("type()")
internal class TypeTest : NativeTest() {

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
        testInstance.tableCreate(0, 0)
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

    @Test
    fun `Error`() {
        testInstance.pushError(ErrorType("error message"))
        assertThat(testInstance.type(1), equalTo(10))
    }

    @Test
    fun `DecimalType`() {
        testInstance.pushDecimal(DecimalType("23.456"))
        assertThat(testInstance.type(1), equalTo(11))
    }
}

@DisplayName("setGlobal()")
internal class SetGlobalTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("getGlobal()")
internal class GetGlobalTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("pushBoolean()")
internal class PushBooleanTest : NativeTest() {
    @Test
    fun `Pushes value on stack`() {
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

@DisplayName("pushDecimal()")
internal class PushDecimalTest : NativeTest() {
    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.pushDecimal(DecimalType("23.23"))
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toDecimal(1), equalTo(DecimalType("23.23")))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999997) { testInstance.pushDecimal(DecimalType(it)) }
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushDecimal(DecimalType(-1))
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("pushFunction()")
internal class PushFunctionTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("pushError()")
internal class PushErrorTest : NativeTest() {
    @Test
    fun `Pushes error value on stack`() {
        val result = testInstance.pushError("some error")
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
    }
}

@DisplayName("pushNil()")
internal class PushNilTest : NativeTest() {

    @Test
    fun `Pushes value on stack`() {
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
internal class PushNumberTest : NativeTest() {

    @Test
    fun `Pushes value on stack`() {
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
internal class PushStringTest : NativeTest() {
    @Test
    fun `Pushes value on stack`() {
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
internal class PopTest : NativeTest() {
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
internal class ToBooleanTest : NativeTest() {

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

@DisplayName("toDecimal()")
internal class ToDecimalTest : NativeTest() {

    @Test
    fun `Tries to read decimal with 0 index`() {
        testInstance.pushDecimal(DecimalType(123))
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toDecimal(0)
        }
        assertThat(exception.message, equalTo("Index must not be 0"))
    }

    @Test
    fun `Tries to read decimal with index bigger than stack size`() {
        testInstance.pushDecimal(DecimalType(123))
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read decimal with abs(negative index) bigger than stack size`() {
        testInstance.pushDecimal(DecimalType(123))
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(-2)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read number as decimal`() {
        testInstance.pushNumber(1.0)
        val exception = assertThrows<IllegalStateException> {
            testInstance.toDecimal(1)
        }
        assertThat(exception.message, equalTo("Expected type to be decimal but was number"))
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushDecimal(DecimalType(123))
        assertThat(testInstance.toDecimal(1), equalTo(DecimalType(123)))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushDecimal(DecimalType(234))
        assertThat(testInstance.toDecimal(2), equalTo(DecimalType(234)))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushDecimal(DecimalType(123))
        assertThat(testInstance.toDecimal(-1), equalTo(DecimalType(123)))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushDecimal(DecimalType(234))
        assertThat(testInstance.toDecimal(-1), equalTo(DecimalType(234)))
        assertThat(testInstance.top(), equalTo(2))
    }
}


@DisplayName("toError()")
internal class ToErrorTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("toNumber()")
internal class ToNumberTest : NativeTest() {
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
internal class ToStringTest : NativeTest() {

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
internal class CreateTableTest : NativeTest() {
    @Test
    fun `Creates an empty table on empty stack`() {
        val result = testInstance.tableCreate(1, 2)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))

        assertThat(testInstance.type(1), equalTo(5))
    }

    @Test
    fun `Array count must not be negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.tableCreate(-1, 0)
        }
        assertThat(exception.message, equalTo("Array count must not be negative"))
    }

    @Test
    fun `Records count must not be negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.tableCreate(0, -1)
        }
        assertThat(exception.message, equalTo("Records count must not be negative"))
    }
}

@DisplayName("tabletSetField()")
internal class TabletSetFieldTest : NativeTest() {
    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same key table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")

        testInstance.pushNumber(42.0)
        testInstance.tabletSetField(1, "key")

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")

        testInstance.pushNumber(42.0)
        testInstance.tabletSetField(1, "different")

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.tableGetField(1, "different")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.tableGetLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value from to not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.tabletSetField(1, "key")
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("tableGetField()")
internal class TableGetFieldTest : NativeTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        val result = testInstance.tableGetField(1, "key")
        assertThat(result, equalTo(4))
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")

        val result = testInstance.tableGetField(1, "does-not-find-anything")
        assertThat(result, equalTo(0))
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.tableGetField(1, "key")
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")

        repeat(999998) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.tableGetField(1, "key")
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("tableGetLength()")
internal class TableGetLengthTest : NativeTest() {
    @Test
    fun `Size of empty table`() {
        testInstance.tableCreate(12, 12)
        val result = testInstance.tableGetLength(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Size of table with single field`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        val result = testInstance.tableGetLength(1)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Size of table with multiple fields`() {
        testInstance.tableCreate(0, 1)
        repeat(10) { idx ->
            testInstance.pushString("value")
            testInstance.tabletSetField(1, "key-${idx}")

            val result = testInstance.tableGetLength(1)
            assertThat(result, equalTo(idx + 1))
        }
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Does not alter the stack`() {
        testInstance.tableCreate(0, 1)
        repeat(10) { idx ->
            testInstance.pushString("value")
            testInstance.tabletSetField(1, "key-${idx}")
            testInstance.tableGetField(1, "Key-${idx}")
        }

        assertThat(testInstance.top(), equalTo(11))

        val result = testInstance.tableGetLength(1)
        assertThat(result, equalTo(10))

        assertThat(testInstance.top(), equalTo(11))
    }

    @Test
    fun `Tries to get table size from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.tableGetLength(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("tableInsert()")
internal class TableInsertTest : NativeTest() {

    @Test
    fun `Insert value to empty table`() {
        testInstance.tableCreate(0, 0)
        testInstance.pushNumber(512.0)
        testInstance.tableAppend(1)

        testInstance.pop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Insert multiple values to table`() {
        testInstance.tableCreate(1000, 0)
        repeat(1000) { idx ->
            testInstance.pushNumber(idx.toDouble())
            val result = testInstance.tableAppend(1)
            assertThat(result, equalTo(idx + 1))
        }
    }
}

@DisplayName("tableSetRaw()")
internal class TableSetRawTest : NativeTest() {
    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.tableGetRaw(1)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same key`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.pushNumber(42.0)
        testInstance.tableSetRaw(1)

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.tableCreate(0, 1)

        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        testInstance.pushString("different")
        testInstance.pushNumber(42.0)
        testInstance.tableSetRaw(1)

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.tableGetField(1, "different")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.tableGetLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value from to not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.pushString("key")
            testInstance.pushString("value")
            testInstance.tableSetRaw(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("tableSetRawIdx()")
internal class TableSetRawIdxTest : NativeTest() {
    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(1, 0)
        testInstance.pushString("value")
        testInstance.tableSetRawIdx(1, 23)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(23.0)
        testInstance.tableGetRaw(1)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same index`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tableSetRawIdx(1, 23)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(42.0)
        testInstance.tableSetRawIdx(1, 23)

        testInstance.pushNumber(23.0)
        testInstance.tableGetRaw(1)
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.tableCreate(0, 1)

        testInstance.pushString("value")
        testInstance.tableSetRawIdx(1, 2)

        testInstance.pushNumber(42.0)
        testInstance.tableSetRawIdx(1, 4)

        testInstance.tableGetRawIdx(1, 2)
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.tableGetRawIdx(1, 4)
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.tableGetLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value from to not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.pushString("value")
            testInstance.tableSetRawIdx(1, 4)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("tableGetRaw()")
internal class TableGetRawTest : NativeTest() {
    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushNumber(23.0)
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        val result = testInstance.tableGetRaw(1)
        assertThat(result, equalTo(3))

        assertThat(testInstance.toNumber(-1), equalTo(23.0))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        testInstance.pushString("does-not-find-anything")
        val result = testInstance.tableGetRaw(1)
        assertThat(result, equalTo(0))

        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.pushString("key")
            testInstance.tableGetRaw(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        repeat(999997) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.pushString("key")
            testInstance.tableGetRaw(1)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("tableGetRawIdx()")
internal class TableGetRawIdxTest : NativeTest() {
    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushNumber(5.0)
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        val result = testInstance.tableGetRawIdx(1, 5)
        assertThat(result, equalTo(4))
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushNumber(43.0)
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        val result = testInstance.tableGetRawIdx(1, 1337)
        assertThat(result, equalTo(0))
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value from not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.tableGetRawIdx(1, 3)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tableSetRawIdx(1, 1)

        repeat(999998) { testInstance.pushBoolean(true) }

        val exception = assertThrows<IllegalArgumentException> {
            testInstance.tableGetRawIdx(1, 1)
        }
        assertThat(exception.message, equalTo("Prevented stack overflow"))
    }
}

@DisplayName("tableNext()")
internal class TableNextTest : NativeTest() {
    @Test
    fun `Next on empty table`() {
        testInstance.tableCreate(0, 0)
        testInstance.pushNil()
        val result = testInstance.tableNext(-2)
        assertThat(result, equalTo(false))

        testInstance.pop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Next on table with single element`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        testInstance.pushNil()
        val result = testInstance.tableNext(1)
        assertThat(result, equalTo(true))
        assertThat(testInstance.toString(-2), equalTo("key"))
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.pop(3)
        verifyStackIsEmpty()
    }

    @Test
    fun `Multiple next`() {
        testInstance.tableCreate(0, 1)

        repeat(1000) { idx ->
            testInstance.pushString("key-${idx}")
            testInstance.pushString("value-${idx}")
            testInstance.tableSetRaw(1)
        }

        val keys = mutableSetOf<String>()
        val values = mutableSetOf<String>()
        testInstance.pushNil()
        repeat(1000) {
            val result = testInstance.tableNext(1)
            assertThat(result, equalTo(true))
            keys.add(testInstance.toString(-2))
            values.add(testInstance.toString(-1))
            testInstance.pop(1)
        }

        assertThat(keys, hasSize(1000))
        assertThat(values, hasSize(1000))

        assertThat(testInstance.tableNext(1), equalTo(false))

        testInstance.pop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Tries to run next on not a table`() {
        testInstance.pushNumber(2.34)
        val exception = assertThrows<IllegalStateException> {
            testInstance.tableNext(1)
        }
        assertThat(exception.message, equalTo("Expected type to be table but was number"))
    }
}

@DisplayName("tableGetSub")
internal class TableGetSubTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("loadString()")
internal class LoadStringTest : NativeTest() {
    @Test
    @Disabled
    fun implementMe() {
    }
}

@DisplayName("call()")
internal class CallTest : NativeTest() {
    @Test
    fun `Calls kotlin function with 2 parameter and 2 receives 2 values back`() {
        testInstance.pushFunction(Magic())
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(5.0)
        testInstance.call(2, 2)

        assertThat(testInstance.toNumber(-1), equalTo(2.0))
        assertThat(testInstance.toNumber(-2), equalTo(20.0))

        testInstance.pop(2)
        verifyStackIsEmpty()
    }

    private class Magic : Function2In2Out<NumberType, NumberType, NumberType, NumberType>(
        FunctionInput2Schema(NumberType::class, NumberType::class),
        FunctionOutput2Schema(NumberType::class, NumberType::class)
    ) {

        override fun invoke(
            ctx: FunctionContext, arg1: NumberType, arg2: NumberType
        ): Pair<NumberType, NumberType> {
            return Pair(arg2 * 4, arg1 * 2)
        }
    }
}

internal sealed class NativeTest {

    val testInstance: Native = run {
        NativeLoader.load(Resources)
        Native(Sandbox(NopSandboxContext()))
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.top(), equalTo(0))
    }
}
