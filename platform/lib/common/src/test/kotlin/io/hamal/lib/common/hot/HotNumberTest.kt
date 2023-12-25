package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger

internal object HotNumberTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", HotNumber(42)::isArray, false),
        Tuple3("isBoolean", HotNumber(42)::isBoolean, false),
        Tuple3("isNumber", HotNumber(42)::isNumber, true),
        Tuple3("isNull", HotNumber(42)::isNull, false),
        Tuple3("isObject", HotNumber(42)::isObject, false),
        Tuple3("isString", HotNumber(42)::isString, false),
        Tuple3("isTerminal", HotNumber(42)::isTerminal, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asNumber", HotNumber(42)::asNumber, HotNumber(42)),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", HotNumber(42)::asArray, "Not HotArray"),
        Tuple3("asBoolean", HotNumber(42)::asBoolean, "Not HotBoolean"),
        Tuple3("asNull", HotNumber(42)::asNull, "Not HotNull"),
        Tuple3("asObject", HotNumber(42)::asObject, "Not HotObject"),
        Tuple3("asString", HotNumber(42)::asString, "Not HotString")
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
        Tuple3("bigDecimalValue", HotNumber(BigDecimal(42))::bigDecimalValue, BigDecimal(42)),
        Tuple3("bigIntegerValue", HotNumber(BigInteger.TEN)::bigIntegerValue, BigInteger.valueOf(10)),
        Tuple3("byteValue", HotNumber((42).toByte())::byteValue, (42).toByte()),
        Tuple3("doubleValue", HotNumber(23.24)::doubleValue, 23.24),
        Tuple3("floatValue", HotNumber(12.13f)::floatValue, 12.13f),
        Tuple3("intValue", HotNumber(42)::intValue, 42),
        Tuple3("longValue", HotNumber(23112023L)::longValue, 23112023L),
        Tuple3("shortValue", HotNumber(Short.MAX_VALUE)::shortValue, Short.MAX_VALUE),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", HotNull::booleanValue, "Not boolean"),
        Tuple3("stringValue", HotNull::stringValue, "Not string")
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