package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


internal object HotBooleanTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", HotBoolean(true)::isArray, false),
        Tuple3("isBoolean", HotBoolean(true)::isBoolean, true),
        Tuple3("isNumber", HotBoolean(true)::isNumber, false),
        Tuple3("isNull", HotBoolean(true)::isNull, false),
        Tuple3("isObject", HotBoolean(true)::isObject, false),
        Tuple3("isString", HotBoolean(true)::isString, false),
        Tuple3("isTerminal", HotBoolean(true)::isTerminal, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asBoolean - true", HotBoolean(true)::asBoolean, HotBoolean(true)),
        Tuple3("asBoolean - false", HotBoolean(false)::asBoolean, HotBoolean(false)),
        Tuple3("asTerminal", HotBoolean(true)::asTerminal, HotBoolean(true)),
        Tuple3("asString - true", HotBoolean(true)::asString, HotString("true")),
        Tuple3("asString - false", HotBoolean(true)::asString, HotString("true"))
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", HotBoolean(true)::asArray, "Not HotArray"),
        Tuple3("asNull", HotBoolean(true)::asNull, "Not HotNull"),
        Tuple3("asNumber", HotBoolean(true)::asNumber, "Not HotNumber"),
        Tuple3("asObject", HotBoolean(true)::asObject, "Not HotObject"),
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
        Tuple3("booleanValue - true", HotBoolean(true)::booleanValue, true),
        Tuple3("booleanValue - false", HotBoolean(false)::booleanValue, false),
        Tuple3("stringValue - true", HotBoolean(true)::stringValue, "true"),
        Tuple3("stringValue - false", HotBoolean(false)::stringValue, "false")
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("bigDecimalValue", HotBoolean(true)::bigDecimalValue, "Not BigDecimal"),
        Tuple3("bigIntegerValue", HotBoolean(true)::bigIntegerValue, "Not BigInteger"),
        Tuple3("byteValue", HotBoolean(true)::byteValue, "Not byte"),
        Tuple3("doubleValue", HotBoolean(true)::doubleValue, "Not double"),
        Tuple3("floatValue", HotBoolean(true)::floatValue, "Not float"),
        Tuple3("intValue", HotBoolean(true)::intValue, "Not int"),
        Tuple3("longValue", HotBoolean(true)::longValue, "Not long"),
        Tuple3("numberValue", HotBoolean(true)::numberValue, "Not number"),
        Tuple3("shortValue", HotBoolean(true)::shortValue, "Not short")
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
        val testInstance = HotBoolean(true)
        val result = testInstance.deepCopy()

        assertTrue(testInstance === result)
    }
}