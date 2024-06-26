package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Decimal
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

internal class JsonObjectTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", JsonObject.empty.isArray, false),
        Tuple3("isBoolean", JsonObject.empty.isBoolean, false),
        Tuple3("isNumber", JsonObject.empty.isNumber, false),
        Tuple3("isNull", JsonObject.empty.isNull, false),
        Tuple3("isObject", JsonObject.empty.isObject, true),
        Tuple3("isString", JsonObject.empty.isString, false),
        Tuple3("isPrimitive", JsonObject.empty.isPrimitive, false)
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
        fun isPrimitive() = listOf(
            Tuple3("isPrimitive", testInstance.isPrimitive("string"), true),
            Tuple3("isPrimitive - not terminal", testInstance.isPrimitive("array"), false),
            Tuple3("isPrimitive - not found", testInstance.isPrimitive("doesNotExists"), false),
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
            Tuple3("asArray", testInstance.asArray("array"), JsonArray.builder().append(42).build()),
            Tuple3("asBoolean", testInstance.asBoolean("boolean"), JsonBoolean(true)),
            Tuple3("asNumber", testInstance.asNumber("number"), JsonNumber(123)),
            Tuple3("asNull", testInstance.asNull("null"), JsonNull),
            Tuple3("asNull - does not found", testInstance.asNull("doesNotExist"), JsonNull),
            Tuple3("asObject", testInstance.asObject("object"), JsonObject.builder().set("hamal", "rocks").build()),
            Tuple3("asString", testInstance.asString("string"), JsonString("SomeString")),
            Tuple3("asPrimitive", testInstance.asPrimitive("string"), JsonString("SomeString")),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun `as - not found`() = listOf<Tuple3<kotlin.String, (kotlin.String) -> Any, kotlin.String>>(
            Tuple3("asArray", testInstance::asArray, "Not JsonArray"),
            Tuple3("asBoolean", testInstance::asBoolean, "Not JsonBoolean"),
            Tuple3("asNumber", testInstance::asNumber, "Not JsonNumber"),
            Tuple3("asObject", testInstance::asObject, "Not JsonObject"),
            Tuple3("asString", testInstance::asString, "Not JsonString"),
            Tuple3("asPrimitive", testInstance::asPrimitive, "Not JsonPrimitive"),
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
        fun `as - wrong type`() = listOf<Tuple4<kotlin.String, kotlin.String, (kotlin.String) -> Any, kotlin.String>>(
            Tuple4("asArray", "object", testInstance::asArray, "Not JsonArray"),
            Tuple4("asBoolean", "string", testInstance::asBoolean, "Not JsonBoolean"),
            Tuple4("asNumber", "boolean", testInstance::asNumber, "Not JsonNumber"),
            Tuple4("asObject", "array", testInstance::asObject, "Not JsonObject"),
            Tuple4("asString", "number", testInstance::asString, "Not JsonString"),
            Tuple4("asPrimitive", "array", testInstance::asPrimitive, "Not JsonPrimitive"),
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
            Tuple3("byteValue", testInstance.byteValue("number"), (123).toByte()),
            Tuple3("decimalValue", testInstance.decimalValue("number"), Decimal(123)),
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
        fun `value - not found`() = listOf<Tuple3<kotlin.String, (kotlin.String) -> Any, kotlin.String>>(
            Tuple3("booleanValue", testInstance::booleanValue, "Not JsonBoolean"),
            Tuple3("byteValue", testInstance::byteValue, "Not JsonNumber"),
            Tuple3("decimalValue", testInstance::decimalValue, "Not JsonNumber"),
            Tuple3("doubleValue", testInstance::doubleValue, "Not JsonNumber"),
            Tuple3("floatValue", testInstance::floatValue, "Not JsonNumber"),
            Tuple3("intValue", testInstance::intValue, "Not JsonNumber"),
            Tuple3("longValue", testInstance::longValue, "Not JsonNumber"),
            Tuple3("shortValue", testInstance::shortValue, "Not JsonNumber"),
            Tuple3("stringValue", testInstance::stringValue, "Not JsonString")
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
        fun `value - wrong type`() = listOf<Tuple4<kotlin.String, kotlin.String, (kotlin.String) -> Any, kotlin.String>>(
            Tuple4("booleanValue", "string", testInstance::booleanValue, "Not JsonBoolean"),
            Tuple4("byteValue", "boolean", testInstance::byteValue, "Not JsonNumber"),
            Tuple4("decimalValue", "boolean", testInstance::decimalValue, "Not JsonNumber"),
            Tuple4("doubleValue", "boolean", testInstance::doubleValue, "Not JsonNumber"),
            Tuple4("floatValue", "boolean", testInstance::floatValue, "Not JsonNumber"),
            Tuple4("intValue", "boolean", testInstance::intValue, "Not JsonNumber"),
            Tuple4("longValue", "boolean", testInstance::longValue, "Not JsonNumber"),
            Tuple4("shortValue", "boolean", testInstance::shortValue, "Not JsonNumber"),
            Tuple4("stringValue", "number", testInstance::stringValue, "Not JsonString")
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
        val testInstance = JsonObject.empty
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

        private val testInstance: JsonObject = JsonObject.builder().set("A", "b").build()
    }

    @Nested
    inner class FindTest {

        @Test
        fun `Finds key`() {
            val result = testInstance.find("A")
            assertThat(result, equalTo(JsonString("b")))
        }

        @Test
        fun `Does not find key`() {
            val result = testInstance.find("b")
            assertThat(result, nullValue())
        }

        private val testInstance: JsonObject = JsonObject.builder().set("A", "b").build()
    }

    @Nested
    inner class GetTest {

        @Test
        fun `Gets key`() {
            val result = testInstance.get("A")
            assertThat(result, equalTo(JsonString("b")))
        }

        @Test
        fun `Does not get key`() {
            assertThrows<NoSuchElementException> {
                testInstance.get("b")
            }.let { exception ->
                assertThat(exception.message, equalTo("b not found"))
            }
        }

        private val testInstance: JsonObject = JsonObject.builder().set("A", "b").build()
    }

    @Nested
    inner class DeepCopyTest {

        @Test
        fun empty() {
            val testInstance = JsonObject.empty
            val result = testInstance.deepCopy()

            assertTrue(testInstance == result)
            assertThat(result.size, equalTo(0))
        }

        @Test
        fun `Copy object`() {
            val result = testInstance.deepCopy()
            assertTrue(testInstance == result)
            assertThat(result.size, equalTo(6))

            assertThat(result.asArray("array"), equalTo(JsonArray.builder().append(42).build()))
            assertThat(result.asBoolean("boolean"), equalTo(JsonBoolean(true)))
            assertThat(result.asNumber("number"), equalTo(JsonNumber(123)))
            assertThat(result.asString("string"), equalTo(JsonString("SomeString")))
            assertThat(result.asObject("object"), equalTo(JsonObject.builder().set("hamal", "rocks").build()))
            assertThat(result.asNull("null"), equalTo(JsonNull))
        }
    }
}

private val testInstance = JsonObject.builder()
    .set("array", JsonArray.builder().append(42).build())
    .set("boolean", true)
    .set("number", 123)
    .set("string", "SomeString")
    .set("object", JsonObject.builder().set("hamal", "rocks").build())
    .setNull("null")
    .build()