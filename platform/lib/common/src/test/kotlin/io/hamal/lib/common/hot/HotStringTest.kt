package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


internal object HotStringTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", HotString("hamal")::isArray, false),
        Tuple3("isBoolean", HotString("hamal")::isBoolean, false),
        Tuple3("isNumber", HotString("hamal")::isNumber, false),
        Tuple3("isNull", HotString("hamal")::isNull, false),
        Tuple3("isObject", HotString("hamal")::isObject, false),
        Tuple3("isString", HotString("hamal")::isString, true),
        Tuple3("isTerminal", HotString("hamal")::isTerminal, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asString", HotString("hamal")::asString, HotString("hamal")),
        Tuple3("asTerminal", HotString("hamal")::asTerminal, HotString("hamal")),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", HotString("hamal")::asArray, "Not HotArray"),
        Tuple3("asBoolean", HotString("hamal")::asBoolean, "Not HotBoolean"),
        Tuple3("asNull", HotString("hamal")::asNull, "Not HotNull"),
        Tuple3("asNumber", HotString("hamal")::asNumber, "Not HotNumber"),
        Tuple3("asObject", HotString("hamal")::asObject, "Not HotObject")
    ).map { (testName, func, expectedMessage) ->
        dynamicTest(testName) {
            assertThrows<IllegalStateException> {
                func()
            }.let { exception ->
                assertThat(
                    exception.message, equalTo(
                        expectedMessage
                    )
                )
            }
        }
    }

    @TestFactory
    fun `value`() = listOf(
        Tuple3("stringValue", HotString("hamal")::stringValue, "hamal")
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", HotString("hamal")::booleanValue, "Not boolean"),
        Tuple3("bigDecimalValue", HotString("hamal")::bigDecimalValue, "Not BigDecimal"),
        Tuple3("bigIntegerValue", HotString("hamal")::bigIntegerValue, "Not BigInteger"),
        Tuple3("byteValue", HotString("hamal")::byteValue, "Not byte"),
        Tuple3("doubleValue", HotString("hamal")::doubleValue, "Not double"),
        Tuple3("floatValue", HotString("hamal")::floatValue, "Not float"),
        Tuple3("intValue", HotString("hamal")::intValue, "Not int"),
        Tuple3("longValue", HotString("hamal")::longValue, "Not long"),
        Tuple3("numberValue", HotString("hamal")::numberValue, "Not number"),
        Tuple3("shortValue", HotString("hamal")::shortValue, "Not short")
    ).map { (testName, func, expectedMessage) ->
        dynamicTest(testName) {
            assertThrows<IllegalStateException> {
                func()
            }.let { exception ->
                assertThat(exception.message, equalTo(expectedMessage))
            }
        }
    }


    @Test
    fun deepCopy() {
        val testInstance = HotString("hamal")
        val result = testInstance.deepCopy()

        assertTrue(testInstance === result)
    }
}