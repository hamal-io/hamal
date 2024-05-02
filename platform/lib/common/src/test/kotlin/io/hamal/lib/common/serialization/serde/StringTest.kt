package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


internal object SerdeStringTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", SerdeString("hamal")::isArray, false),
        Tuple3("isBoolean", SerdeString("hamal")::isBoolean, false),
        Tuple3("isNumber", SerdeString("hamal")::isNumber, false),
        Tuple3("isNull", SerdeString("hamal")::isNull, false),
        Tuple3("isObject", SerdeString("hamal")::isObject, false),
        Tuple3("isString", SerdeString("hamal")::isString, true),
        Tuple3("isTerminal", SerdeString("hamal")::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asString", SerdeString("hamal")::asString, SerdeString("hamal")),
        Tuple3("asTerminal", SerdeString("hamal")::asPrimitive, SerdeString("hamal")),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", SerdeString("hamal")::asArray, "Not HotArray"),
        Tuple3("asBoolean", SerdeString("hamal")::asBoolean, "Not HotBoolean"),
        Tuple3("asNull", SerdeString("hamal")::asNull, "Not HotNull"),
        Tuple3("asNumber", SerdeString("hamal")::asNumber, "Not HotNumber"),
        Tuple3("asObject", SerdeString("hamal")::asObject, "Not HotObject")
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
        Tuple3("stringValue", SerdeString("hamal")::stringValue, "hamal")
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", SerdeString("hamal")::booleanValue, "Not Boolean"),
        Tuple3("byteValue", SerdeString("hamal")::byteValue, "Not Byte"),
        Tuple3("decimalValue", SerdeString("hamal")::decimalValue, "Not Decimal"),
        Tuple3("doubleValue", SerdeString("hamal")::doubleValue, "Not Double"),
        Tuple3("floatValue", SerdeString("hamal")::floatValue, "Not Float"),
        Tuple3("intValue", SerdeString("hamal")::intValue, "Not Int"),
        Tuple3("longValue", SerdeString("hamal")::longValue, "Not Long"),
        Tuple3("numberValue", SerdeString("hamal")::numberValue, "Not Number"),
        Tuple3("shortValue", SerdeString("hamal")::shortValue, "Not Short")
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
        val testInstance = SerdeString("hamal")
        val result = testInstance.deepCopy()

        assertTrue(testInstance == result)
    }
}