package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object JsonNumberTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", JsonNumber(42)::isArray, false),
        Tuple3("isBoolean", JsonNumber(42)::isBoolean, false),
        Tuple3("isNumber", JsonNumber(42)::isNumber, true),
        Tuple3("isNull", JsonNumber(42)::isNull, false),
        Tuple3("isObject", JsonNumber(42)::isObject, false),
        Tuple3("isString", JsonNumber(42)::isString, false),
        Tuple3("isPrimitive", JsonNumber(42)::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asNumber", JsonNumber(42)::asNumber, JsonNumber(42)),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", JsonNumber(42)::asArray, "Not JsonArray"),
        Tuple3("asBoolean", JsonNumber(42)::asBoolean, "Not JsonBoolean"),
        Tuple3("asNull", JsonNumber(42)::asNull, "Not JsonNull"),
        Tuple3("asObject", JsonNumber(42)::asObject, "Not JsonObject"),
        Tuple3("asString", JsonNumber(42)::asString, "Not JsonString")
    ).map { (testName, func, expectedMessage) ->
        dynamicTest(testName) {
            assertThrows<IllegalStateException> {
                func()
            }.let { exception ->
                assertThat(exception.message, equalTo(expectedMessage))
            }
        }
    }

    @TestFactory
    fun `value valid`() = listOf(
        Tuple3("decimalValue", JsonNumber(Decimal(42))::decimalValue, Decimal(42)),
        Tuple3("byteValue", JsonNumber((42).toByte())::byteValue, (42).toByte()),
        Tuple3("doubleValue", JsonNumber(23.24)::doubleValue, 23.24),
        Tuple3("floatValue", JsonNumber(12.13f)::floatValue, 12.13f),
        Tuple3("intValue", JsonNumber(42)::intValue, 42),
        Tuple3("longValue", JsonNumber(23112023L)::longValue, 23112023L),
        Tuple3("shortValue", JsonNumber(Short.MAX_VALUE)::shortValue, Short.MAX_VALUE),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", JsonNull::booleanValue, "Not Boolean"),
        Tuple3("stringValue", JsonNull::stringValue, "Not String")
    ).map { (testName, func, expectedMessage) ->
        dynamicTest(testName) {
            assertThrows<IllegalStateException> {
                func()
            }.let { exception ->
                assertThat(exception.message, equalTo(expectedMessage))
            }
        }
    }
}