package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal object SerdeBooleanTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", SerdeBoolean(true)::isArray, false),
        Tuple3("isBoolean", SerdeBoolean(true)::isBoolean, true),
        Tuple3("isNumber", SerdeBoolean(true)::isNumber, false),
        Tuple3("isNull", SerdeBoolean(true)::isNull, false),
        Tuple3("isObject", SerdeBoolean(true)::isObject, false),
        Tuple3("isString", SerdeBoolean(true)::isString, false),
        Tuple3("isTerminal", SerdeBoolean(true)::isPrimitive, true)
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = listOf(
        Tuple3("asBoolean - true", SerdeBoolean(true)::asBoolean, SerdeBoolean(true)),
        Tuple3("asBoolean - false", SerdeBoolean(false)::asBoolean, SerdeBoolean(false)),
        Tuple3("asTerminal", SerdeBoolean(true)::asPrimitive, SerdeBoolean(true)),
        Tuple3("asString - true", SerdeBoolean(true)::asString, SerdeString("true")),
        Tuple3("asString - false", SerdeBoolean(true)::asString, SerdeString("true"))
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = listOf(
        Tuple3("asArray", SerdeBoolean(true)::asArray, "Not HotArray"),
        Tuple3("asNull", SerdeBoolean(true)::asNull, "Not HotNull"),
        Tuple3("asNumber", SerdeBoolean(true)::asNumber, "Not HotNumber"),
        Tuple3("asObject", SerdeBoolean(true)::asObject, "Not HotObject"),
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
        Tuple3("booleanValue - true", SerdeBoolean(true)::booleanValue, true),
        Tuple3("booleanValue - false", SerdeBoolean(false)::booleanValue, false),
        Tuple3("stringValue - true", SerdeBoolean(true)::stringValue, "true"),
        Tuple3("stringValue - false", SerdeBoolean(false)::stringValue, "false")
    ).map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `value invalid`() = listOf(
        Tuple3("byteValue", SerdeBoolean(true)::byteValue, "Not Byte"),
        Tuple3("decimalValue", SerdeBoolean(true)::decimalValue, "Not Decimal"),
        Tuple3("doubleValue", SerdeBoolean(true)::doubleValue, "Not Double"),
        Tuple3("floatValue", SerdeBoolean(true)::floatValue, "Not Float"),
        Tuple3("intValue", SerdeBoolean(true)::intValue, "Not Int"),
        Tuple3("longValue", SerdeBoolean(true)::longValue, "Not Long"),
        Tuple3("numberValue", SerdeBoolean(true)::numberValue, "Not Number"),
        Tuple3("shortValue", SerdeBoolean(true)::shortValue, "Not Short")
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
        val testInstance = SerdeBoolean(true)
        val result = testInstance.deepCopy()

        assertTrue(testInstance == result)
    }
}