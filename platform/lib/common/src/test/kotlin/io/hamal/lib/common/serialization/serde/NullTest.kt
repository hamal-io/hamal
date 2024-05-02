package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object SerdeNullTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", SerdeNull::isArray, false),
        Tuple3("isBoolean", SerdeNull::isBoolean, false),
        Tuple3("isNumber", SerdeNull::isNumber, false),
        Tuple3("isNull", SerdeNull::isNull, true),
        Tuple3("isObject", SerdeNull::isObject, false),
        Tuple3("isString", SerdeNull::isString, false),
        Tuple3("isTerminal", SerdeNull::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }


    @TestFactory
    fun `as`() = listOf(
        Tuple3("asNull", SerdeNull::asNull, SerdeNull),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", SerdeNull::asArray, "Not HotArray"),
        Tuple3("asBoolean", SerdeNull::asBoolean, "Not HotBoolean"),
        Tuple3("asNumber", SerdeNull::asNumber, "Not HotNumber"),
        Tuple3("asObject", SerdeNull::asObject, "Not HotObject"),
        Tuple3("asString", SerdeNull::asString, "Not HotString")
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
        Tuple3("booleanValue", SerdeNull::booleanValue, "Not Boolean"),
        Tuple3("byteValue", SerdeNull::byteValue, "Not Byte"),
        Tuple3("decimalValue", SerdeNull::decimalValue, "Not Decimal"),
        Tuple3("doubleValue", SerdeNull::doubleValue, "Not Double"),
        Tuple3("floatValue", SerdeNull::floatValue, "Not Float"),
        Tuple3("intValue", SerdeNull::intValue, "Not Int"),
        Tuple3("longValue", SerdeNull::longValue, "Not Long"),
        Tuple3("shortValue", SerdeNull::shortValue, "Not Short"),
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