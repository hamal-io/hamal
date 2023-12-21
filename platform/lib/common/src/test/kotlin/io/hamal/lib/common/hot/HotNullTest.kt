package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object HotNullTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", HotNull::isArray, false),
        Tuple3("isBoolean", HotNull::isBoolean, false),
        Tuple3("isNumber", HotNull::isNumber, false),
        Tuple3("isNull", HotNull::isNull, true),
        Tuple3("isObject", HotNull::isObject, false),
        Tuple3("isString", HotNull::isString, false),
        Tuple3("isTerminal", HotNull::isTerminal, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }


    @TestFactory
    fun `as`() = listOf(
        Tuple3("asNull", HotNull::asNull, HotNull),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", HotNull::asArray, "Not HotArray"),
        Tuple3("asBoolean", HotNull::asBoolean, "Not HotBoolean"),
        Tuple3("asNumber", HotNull::asNumber, "Not HotNumber"),
        Tuple3("asObject", HotNull::asObject, "Not HotObject"),
        Tuple3("asString", HotNull::asString, "Not HotString")
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
    fun `value invalid`() = listOf(
        Tuple3("booleanValue", HotNull::booleanValue, "Not boolean"),
        Tuple3("bigDecimalValue", HotNull::bigDecimalValue, "Not BigDecimal"),
        Tuple3("bigIntegerValue", HotNull::bigIntegerValue, "Not BigInteger"),
        Tuple3("byteValue", HotNull::byteValue, "Not byte"),
        Tuple3("doubleValue", HotNull::doubleValue, "Not double"),
        Tuple3("floatValue", HotNull::floatValue, "Not float"),
        Tuple3("intValue", HotNull::intValue, "Not int"),
        Tuple3("longValue", HotNull::longValue, "Not long"),
        Tuple3("numberValue", HotNull::numberValue, "Not number"),
        Tuple3("shortValue", HotNull::shortValue, "Not short"),
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