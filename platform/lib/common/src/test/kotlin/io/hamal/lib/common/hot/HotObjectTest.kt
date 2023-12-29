package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import io.hamal.lib.common.Tuple4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger

internal class HotObjectTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", HotObject.empty.isArray, false),
        Tuple3("isBoolean", HotObject.empty.isBoolean, false),
        Tuple3("isNumber", HotObject.empty.isNumber, false),
        Tuple3("isNull", HotObject.empty.isNull, false),
        Tuple3("isObject", HotObject.empty.isObject, true),
        Tuple3("isString", HotObject.empty.isString, false),
        Tuple3("isTerminal", HotObject.empty.isTerminal, false)
    ).map { (testName, value, expected) ->
        dynamicTest(testName) {
            assertThat(value, equalTo(expected))
        }
    }

    @Nested
    inner class IsTest {

        @TestFactory
        fun isArray() = listOf(
            Tuple3("isArray", testInstance.isArray("array"), true),
            Tuple3("isArray - not array", testInstance.isArray("boolean"), false),
            Tuple3("isArray - not found", testInstance.isArray("doesNotExists"), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isBoolean() = listOf(
            Tuple3("isBoolean", testInstance.isBoolean("boolean"), true),
            Tuple3("isBoolean - not boolean", testInstance.isBoolean("number"), false),
            Tuple3("isBoolean - not found", testInstance.isBoolean("doesNotExists"), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isNumber() = listOf(
            Tuple3("isNumber", testInstance.isNumber("number"), true),
            Tuple3("isNumber - not number", testInstance.isNumber("boolean"), false),
            Tuple3("isNumber - not found", testInstance.isNumber("doesNotExists"), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isNull() = listOf(
            Tuple3("isNull", testInstance.isNull("null"), true),
            Tuple3("isNull - not null", testInstance.isNull("boolean"), false),
            Tuple3("isNull - not found", testInstance.isNull("doesNotExists"), true),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isObject() = listOf(
            Tuple3("isObject", testInstance.isObject("object"), true),
            Tuple3("isObject - not string", testInstance.isObject("boolean"), false),
            Tuple3("isObject - not found", testInstance.isObject("doesNotExists"), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isString() = listOf(
            Tuple3("isString", testInstance.isString("string"), true),
            Tuple3("isString - not string", testInstance.isString("boolean"), false),
            Tuple3("isString - not found", testInstance.isString("doesNotExists"), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isTerminal() = listOf(
            Tuple3("isTerminal", testInstance.isTerminal("string"), true),
            Tuple3("isTerminal - not terminal", testInstance.isTerminal("array"), false),
            Tuple3("isTerminal - not found", testInstance.isTerminal("doesNotExists"), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }
    }

    @Nested
    inner class AsTest {

        @TestFactory
        fun `as`() = listOf(
            Tuple3("asArray", testInstance.asArray("array"), HotArray.builder().add(42).build()),
            Tuple3("asBoolean", testInstance.asBoolean("boolean"), HotBoolean(true)),
            Tuple3("asNumber", testInstance.asNumber("number"), HotNumber(123)),
            Tuple3("asNull", testInstance.asNull("null"), HotNull),
            Tuple3("asNull - does not found", testInstance.asNull("doesNotExist"), HotNull),
            Tuple3("asObject", testInstance.asObject("object"), HotObject.builder().set("hamal", "rocks").build()),
            Tuple3("asString", testInstance.asString("string"), HotString("SomeString")),
            Tuple3("asTerminal", testInstance.asTerminal("string"), HotString("SomeString")),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun `as - not found`() = listOf<Tuple3<String, (String) -> Any, String>>(
            Tuple3("asArray", testInstance::asArray, "Not HotArray"),
            Tuple3("asBoolean", testInstance::asBoolean, "Not HotBoolean"),
            Tuple3("asNumber", testInstance::asNumber, "Not HotNumber"),
            Tuple3("asObject", testInstance::asObject, "Not HotObject"),
            Tuple3("asString", testInstance::asString, "Not HotString"),
            Tuple3("asTerminal", testInstance::asTerminal, "Not HotTerminal"),
        ).map { (testName, fn, expectedErrorMessage) ->
            dynamicTest(testName) {
                assertThrows<IllegalStateException> {
                    fn("doesNotExists")
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }

        @TestFactory
        fun `as - wrong type`() = listOf<Tuple4<String, String, (String) -> Any, String>>(
            Tuple4("asArray", "object", testInstance::asArray, "Not HotArray"),
            Tuple4("asBoolean", "string", testInstance::asBoolean, "Not HotBoolean"),
            Tuple4("asNumber", "boolean", testInstance::asNumber, "Not HotNumber"),
            Tuple4("asObject", "array", testInstance::asObject, "Not HotObject"),
            Tuple4("asString", "number", testInstance::asString, "Not HotString"),
            Tuple4("asTerminal", "array", testInstance::asTerminal, "Not HotTerminal"),
        ).map { (testName, key, fn, expectedErrorMessage) ->
            dynamicTest(testName) {
                assertThrows<IllegalStateException> {
                    fn(key)
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }

    }

    @Nested
    inner class ValueTest {

        @TestFactory
        fun `value`() = listOf(
            Tuple3("booleanValue", testInstance.booleanValue("boolean"), true),
            Tuple3("bigDecimalValue", testInstance.bigDecimalValue("number"), BigDecimal(123)),
            Tuple3("bigIntegerValue", testInstance.bigIntegerValue("number"), BigInteger.valueOf(123)),
            Tuple3("byteValue", testInstance.byteValue("number"), (123).toByte()),
            Tuple3("doubleValue", testInstance.doubleValue("number"), 123.0),
            Tuple3("floatValue", testInstance.floatValue("number"), 123.0f),
            Tuple3("intValue", testInstance.intValue("number"), 123),
            Tuple3("longValue", testInstance.longValue("number"), 123L),
            Tuple3("shortValue", testInstance.shortValue("number"), (123).toShort()),
            Tuple3("stringValue", testInstance.stringValue("string"), "SomeString")
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun `value - not found`() = listOf<Tuple3<String, (String) -> Any, String>>(
            Tuple3("booleanValue", testInstance::booleanValue, "Not HotBoolean"),
            Tuple3("bigDecimalValue", testInstance::bigDecimalValue, "Not HotNumber"),
            Tuple3("bigIntegerValue", testInstance::bigIntegerValue, "Not HotNumber"),
            Tuple3("byteValue", testInstance::byteValue, "Not HotNumber"),
            Tuple3("doubleValue", testInstance::doubleValue, "Not HotNumber"),
            Tuple3("floatValue", testInstance::floatValue, "Not HotNumber"),
            Tuple3("intValue", testInstance::intValue, "Not HotNumber"),
            Tuple3("longValue", testInstance::longValue, "Not HotNumber"),
            Tuple3("shortValue", testInstance::shortValue, "Not HotNumber"),
            Tuple3("stringValue", testInstance::stringValue, "Not HotString")
        ).map { (testName, fn, expectedErrorMessage) ->
            dynamicTest(testName) {
                assertThrows<IllegalStateException> {
                    fn("doesNotExists")
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }

        @TestFactory
        fun `value - wrong type`() = listOf<Tuple4<String, String, (String) -> Any, String>>(
            Tuple4("booleanValue", "string", testInstance::booleanValue, "Not HotBoolean"),
            Tuple4("bigDecimalValue", "boolean", testInstance::bigDecimalValue, "Not HotNumber"),
            Tuple4("bigIntegerValue", "boolean", testInstance::bigIntegerValue, "Not HotNumber"),
            Tuple4("byteValue", "boolean", testInstance::byteValue, "Not HotNumber"),
            Tuple4("doubleValue", "boolean", testInstance::doubleValue, "Not HotNumber"),
            Tuple4("floatValue", "boolean", testInstance::floatValue, "Not HotNumber"),
            Tuple4("intValue", "boolean", testInstance::intValue, "Not HotNumber"),
            Tuple4("longValue", "boolean", testInstance::longValue, "Not HotNumber"),
            Tuple4("shortValue", "boolean", testInstance::shortValue, "Not HotNumber"),
            Tuple4("stringValue", "number", testInstance::stringValue, "Not HotString")
        ).map { (testName, key, fn, expectedErrorMessage) ->
            dynamicTest(testName) {
                assertThrows<IllegalStateException> {
                    fn(key)
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }
    }


    @Test
    fun empty() {
        val testInstance = HotObject.empty
        assertThat(testInstance.size, equalTo(0))
    }


    @Nested
    inner class ContainsKeyTest {

        @Test
        fun `Contains key`() {
            assertTrue(testInstance.containsKey("A"))
        }

        @Test
        fun `Contains not key`() {
            assertFalse(testInstance.containsKey("b"))
        }

        private val testInstance: HotObject = HotObject.builder().set("A", "b").build()
    }

    @Nested
    inner class FindTest {

        @Test
        fun `Finds key`() {
            val result = testInstance.find("A")
            assertThat(result, equalTo(HotString("b")))
        }

        @Test
        fun `Does not find key`() {
            val result = testInstance.find("b")
            assertThat(result, nullValue())
        }

        private val testInstance: HotObject = HotObject.builder().set("A", "b").build()
    }

    @Nested
    inner class GetTest {

        @Test
        fun `Gets key`() {
            val result = testInstance.get("A")
            assertThat(result, equalTo(HotString("b")))
        }

        @Test
        fun `Does not get key`() {
            assertThrows<NoSuchElementException> {
                testInstance.get("b")
            }.let { exception ->
                assertThat(exception.message, equalTo("b not found"))
            }
        }

        private val testInstance: HotObject = HotObject.builder().set("A", "b").build()
    }

    @Nested
    inner class DeepCopyTest {

        @Test
        fun empty() {
            val testInstance = HotObject.empty
            val result = testInstance.deepCopy()

            assertFalse(testInstance === result)
            require(result is HotObject)
            assertThat(result.size, equalTo(0))
        }

        @Test
        fun `Copy object`() {
            val result = testInstance.deepCopy()
            assertFalse(testInstance === result)
            require(result is HotObject)
            assertThat(result.size, equalTo(6))

            assertThat(result.asArray("array"), equalTo(HotArray.builder().add(42).build()))
            assertThat(result.asBoolean("boolean"), equalTo(HotBoolean(true)))
            assertThat(result.asNumber("number"), equalTo(HotNumber(123)))
            assertThat(result.asString("string"), equalTo(HotString("SomeString")))
            assertThat(result.asObject("object"), equalTo(HotObject.builder().set("hamal", "rocks").build()))
            assertThat(result.asNull("null"), equalTo(HotNull))
        }
    }
}

private val testInstance = HotObject.builder()
    .set("array", HotArray.builder().add(42).build())
    .set("boolean", true)
    .set("number", 123)
    .set("string", "SomeString")
    .set("object", HotObject.builder().set("hamal", "rocks").build())
    .setNull("null")
    .build()