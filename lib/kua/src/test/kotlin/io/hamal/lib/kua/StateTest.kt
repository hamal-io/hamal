package io.hamal.lib.kua

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("versionNumber()")
internal class VersionNumberTest : BaseStateTest() {
    @Test
    fun `Loads current lua version number`() {
        val result = testInstance.versionNumber()
        assertThat("5.4", result, equalTo(504))
    }
}

@DisplayName("integerWidth()")
internal class IntegerWidthTest : BaseStateTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.integerWidth()
        assertThat("64bit platform - 8 byte", result, equalTo(8))
    }
}

@DisplayName("size()")
internal class SizeTest : BaseStateTest() {
    @Test
    fun `Nothing pushed on the stack`() {
        val result = testInstance.size()
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Pushing to the stack cause stack to grow`() {
        repeat(100) { idx ->
            assertThat(testInstance.size(), equalTo(idx))
            testInstance.pushBoolean(true)
        }
    }
}

@DisplayName("type()")
internal class TypeTest : BaseStateTest() {

    @Test
    fun `Tries to get type with negative index`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.type(-1)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.type(0)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read boolean with index bigger than stack size`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.type(2)
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
}

@DisplayName("pushNil()")
internal class PushNilTest : BaseStateTest() {
    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushNil()
        assertThat(result, equalTo(1))
        assertThat(testInstance.size(), equalTo(1))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushNil() }

        val exception = assertThrows<StackOverflowError> {
            testInstance.pushNil()
        }
        assertThat(exception.message, equalTo("StackOverflow - Its all part of the process"))
    }
}


@DisplayName("pushBoolean()")
internal class PushBooleanTest : BaseStateTest() {
    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushBoolean(true)
        assertThat(result, equalTo(1))
        assertThat(testInstance.size(), equalTo(1))
        assertThat(testInstance.toBoolean(1), equalTo(true))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushBoolean(true) }

        val exception = assertThrows<StackOverflowError> {
            testInstance.pushBoolean(true)
        }
        assertThat(exception.message, equalTo("StackOverflow - Its all part of the process"))
    }
}

@DisplayName("pushNumber()")
internal class PushNumberTest : BaseStateTest() {
    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushNumber(13.37)
        assertThat(result, equalTo(1))
        assertThat(testInstance.size(), equalTo(1))
        assertThat(testInstance.toNumber(1), equalTo(13.37))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushNumber(it.toDouble()) }
        val exception = assertThrows<StackOverflowError> {
            testInstance.pushNumber(-1.0)
        }
        assertThat(exception.message, equalTo("StackOverflow - Its all part of the process"))
    }
}

@DisplayName("pushString()")
internal class PushStringTest : BaseStateTest() {
    @Test
    fun `Pushes value to stack`() {
        val result = testInstance.pushString("hamal")
        assertThat(result, equalTo(1))
        assertThat(testInstance.size(), equalTo(1))
        assertThat(testInstance.toString(1), equalTo("hamal"))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushString("code-sleep-repeat") }

        val exception = assertThrows<StackOverflowError> {
            testInstance.pushString("until you can not anymore")
        }
        assertThat(exception.message, equalTo("StackOverflow - Its all part of the process"))
    }
}

@DisplayName("toBoolean()")
internal class ToBooleanTest : BaseStateTest() {

    @Test
    fun `Tries to read boolean with negative index`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(-1)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
    }

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.pushBoolean(true)
        val exception = assertThrows<IllegalArgumentException> {
            testInstance.toBoolean(0)
        }
        assertThat(exception.message, equalTo("Index out of bounds"))
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
    fun `Reads value on stack without popping the value`() {
        testInstance.pushBoolean(true)
        assertThat(testInstance.toBoolean(1), equalTo(true))
        assertThat(testInstance.size(), equalTo(1))

        testInstance.pushBoolean(false)
        assertThat(testInstance.toBoolean(2), equalTo(false))
        assertThat(testInstance.size(), equalTo(2))
    }
}


internal sealed class BaseStateTest {
    val testInstance: LuaState = run {
        ResourceLoader.load()
        LuaState()
    }
}
