package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object JsonNullTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", JsonNull::isArray, false),
        Tuple3("isBoolean", JsonNull::isBoolean, false),
        Tuple3("isNumber", JsonNull::isNumber, false),
        Tuple3("isNull", JsonNull::isNull, true),
        Tuple3("isObject", JsonNull::isObject, false),
        Tuple3("isString", JsonNull::isString, false),
        Tuple3("isPrimitive", JsonNull::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }


    @TestFactory
    fun `as`() = listOf(
        Tuple3("asNull", JsonNull::asNull, JsonNull),
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", JsonNull::asArray, "Not HotArray"),
        Tuple3("asBoolean", JsonNull::asBoolean, "Not HotBoolean"),
        Tuple3("asNumber", JsonNull::asNumber, "Not HotNumber"),
        Tuple3("asObject", JsonNull::asObject, "Not HotObject"),
        Tuple3("asString", JsonNull::asString, "Not HotString")
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
        Tuple3("booleanValue", JsonNull::booleanValue, "Not Boolean"),
        Tuple3("byteValue", JsonNull::byteValue, "Not Byte"),
        Tuple3("decimalValue", JsonNull::decimalValue, "Not Decimal"),
        Tuple3("doubleValue", JsonNull::doubleValue, "Not Double"),
        Tuple3("floatValue", JsonNull::floatValue, "Not Float"),
        Tuple3("intValue", JsonNull::intValue, "Not Int"),
        Tuple3("longValue", JsonNull::longValue, "Not Long"),
        Tuple3("shortValue", JsonNull::shortValue, "Not Short"),
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