package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object JsonBooleanTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", JsonBoolean(true)::isArray, false),
        Tuple3("isBoolean", JsonBoolean(true)::isBoolean, true),
        Tuple3("isNumber", JsonBoolean(true)::isNumber, false),
        Tuple3("isNull", JsonBoolean(true)::isNull, false),
        Tuple3("isObject", JsonBoolean(true)::isObject, false),
        Tuple3("isString", JsonBoolean(true)::isString, false),
        Tuple3("isPrimitive", JsonBoolean(true)::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asBoolean - true", JsonBoolean(true)::asBoolean, JsonBoolean(true)),
        Tuple3("asBoolean - false", JsonBoolean(false)::asBoolean, JsonBoolean(false)),
        Tuple3("asPrimitive", JsonBoolean(true)::asPrimitive, JsonBoolean(true)),
        Tuple3("asString - true", JsonBoolean(true)::asString, JsonString("true")),
        Tuple3("asString - false", JsonBoolean(true)::asString, JsonString("true"))
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", JsonBoolean(true)::asArray, "Not HotArray"),
        Tuple3("asNull", JsonBoolean(true)::asNull, "Not HotNull"),
        Tuple3("asNumber", JsonBoolean(true)::asNumber, "Not HotNumber"),
        Tuple3("asObject", JsonBoolean(true)::asObject, "Not HotObject"),
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
        Tuple3("booleanValue - true", JsonBoolean(true)::booleanValue, true),
        Tuple3("booleanValue - false", JsonBoolean(false)::booleanValue, false),
        Tuple3("stringValue - true", JsonBoolean(true)::stringValue, "true"),
        Tuple3("stringValue - false", JsonBoolean(false)::stringValue, "false")
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("byteValue", JsonBoolean(true)::byteValue, "Not Byte"),
        Tuple3("decimalValue", JsonBoolean(true)::decimalValue, "Not Decimal"),
        Tuple3("doubleValue", JsonBoolean(true)::doubleValue, "Not Double"),
        Tuple3("floatValue", JsonBoolean(true)::floatValue, "Not Float"),
        Tuple3("intValue", JsonBoolean(true)::intValue, "Not Int"),
        Tuple3("longValue", JsonBoolean(true)::longValue, "Not Long"),
        Tuple3("numberValue", JsonBoolean(true)::numberValue, "Not Number"),
        Tuple3("shortValue", JsonBoolean(true)::shortValue, "Not Short")
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
        val testInstance = JsonBoolean(true)
        val result = testInstance.deepCopy()

        assertTrue(testInstance == result)
    }
}