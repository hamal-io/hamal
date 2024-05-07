package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


internal object JsonStringTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", JsonString("hamal")::isArray, false),
        Tuple3("isBoolean", JsonString("hamal")::isBoolean, false),
        Tuple3("isNumber", JsonString("hamal")::isNumber, false),
        Tuple3("isNull", JsonString("hamal")::isNull, false),
        Tuple3("isObject", JsonString("hamal")::isObject, false),
        Tuple3("isString", JsonString("hamal")::isString, true),
        Tuple3("isPrimitive", JsonString("hamal")::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asString", JsonString("hamal")::asString, JsonString("hamal")),
        Tuple3("asPrimitive", JsonString("hamal")::asPrimitive, JsonString("hamal")),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", JsonString("hamal")::asArray, "Not JsonArray"),
        Tuple3("asBoolean", JsonString("hamal")::asBoolean, "Not JsonBoolean"),
        Tuple3("asNull", JsonString("hamal")::asNull, "Not JsonNull"),
        Tuple3("asNumber", JsonString("hamal")::asNumber, "Not JsonNumber"),
        Tuple3("asObject", JsonString("hamal")::asObject, "Not JsonObject")
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
        Tuple3("stringValue", JsonString("hamal")::stringValue, "hamal")
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", JsonString("hamal")::booleanValue, "Not Boolean"),
        Tuple3("byteValue", JsonString("hamal")::byteValue, "Not Byte"),
        Tuple3("decimalValue", JsonString("hamal")::decimalValue, "Not Decimal"),
        Tuple3("doubleValue", JsonString("hamal")::doubleValue, "Not Double"),
        Tuple3("floatValue", JsonString("hamal")::floatValue, "Not Float"),
        Tuple3("intValue", JsonString("hamal")::intValue, "Not Int"),
        Tuple3("longValue", JsonString("hamal")::longValue, "Not Long"),
        Tuple3("numberValue", JsonString("hamal")::numberValue, "Not Number"),
        Tuple3("shortValue", JsonString("hamal")::shortValue, "Not Short")
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
        val testInstance = JsonString("hamal")
        val result = testInstance.deepCopy()

        assertTrue(testInstance == result)
    }
}