package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object SerdeNumberTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", SerdeNumber(42)::isArray, false),
        Tuple3("isBoolean", SerdeNumber(42)::isBoolean, false),
        Tuple3("isNumber", SerdeNumber(42)::isNumber, true),
        Tuple3("isNull", SerdeNumber(42)::isNull, false),
        Tuple3("isObject", SerdeNumber(42)::isObject, false),
        Tuple3("isString", SerdeNumber(42)::isString, false),
        Tuple3("isTerminal", SerdeNumber(42)::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asNumber", SerdeNumber(42)::asNumber, SerdeNumber(42)),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", SerdeNumber(42)::asArray, "Not HotArray"),
        Tuple3("asBoolean", SerdeNumber(42)::asBoolean, "Not HotBoolean"),
        Tuple3("asNull", SerdeNumber(42)::asNull, "Not HotNull"),
        Tuple3("asObject", SerdeNumber(42)::asObject, "Not HotObject"),
        Tuple3("asString", SerdeNumber(42)::asString, "Not HotString")
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
        Tuple3("decimalValue", SerdeNumber(Decimal(42))::decimalValue, Decimal(42)),
        Tuple3("byteValue", SerdeNumber((42).toByte())::byteValue, (42).toByte()),
        Tuple3("doubleValue", SerdeNumber(23.24)::doubleValue, 23.24),
        Tuple3("floatValue", SerdeNumber(12.13f)::floatValue, 12.13f),
        Tuple3("intValue", SerdeNumber(42)::intValue, 42),
        Tuple3("longValue", SerdeNumber(23112023L)::longValue, 23112023L),
        Tuple3("shortValue", SerdeNumber(Short.MAX_VALUE)::shortValue, Short.MAX_VALUE),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", SerdeNull::booleanValue, "Not Boolean"),
        Tuple3("stringValue", SerdeNull::stringValue, "Not String")
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