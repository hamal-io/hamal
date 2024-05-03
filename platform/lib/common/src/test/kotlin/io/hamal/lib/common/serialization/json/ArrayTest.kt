package io.hamal.lib.common.serialization.json

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.Tuple3
import io.hamal.lib.common.Tuple4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class JsonArrayTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", JsonArray.empty.isArray, true),
        Tuple3("isBoolean", JsonArray.empty.isBoolean, false),
        Tuple3("isNumber", JsonArray.empty.isNumber, false),
        Tuple3("isNull", JsonArray.empty.isNull, false),
        Tuple3("isObject", JsonArray.empty.isObject, false),
        Tuple3("isString", JsonArray.empty.isString, false),
        Tuple3("isPrimitive", JsonArray.empty.isPrimitive, false)
    ).map { (testName, value, expected) ->
        dynamicTest(testName) {
            assertThat(value, equalTo(expected))
        }
    }

    @Nested
    inner class IsTest {

        @TestFactory
        fun isArray() = listOf(
            Tuple3("isArray", testInstance.isArray(arrayIdx), true),
            Tuple3("isArray - not array", testInstance.isArray(booleanIdx), false),
            Tuple3("isArray - not found", testInstance.isArray(doesNotExistIdx), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isBoolean() = listOf(
            Tuple3("isBoolean", testInstance.isBoolean(booleanIdx), true),
            Tuple3("isBoolean - not boolean", testInstance.isBoolean(numberIdx), false),
            Tuple3("isBoolean - not found", testInstance.isBoolean(doesNotExistIdx), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isNumber() = listOf(
            Tuple3("isNumber", testInstance.isNumber(numberIdx), true),
            Tuple3("isNumber - not number", testInstance.isNumber(booleanIdx), false),
            Tuple3("isNumber - not found", testInstance.isNumber(doesNotExistIdx), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isNull() = listOf(
            Tuple3("isNull", testInstance.isNull(nullIdx), true),
            Tuple3("isNull - not null", testInstance.isNull(booleanIdx), false),
            Tuple3("isNull - not found", testInstance.isNull(doesNotExistIdx), true),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isObject() = listOf(
            Tuple3("isObject", testInstance.isObject(objectIdx), true),
            Tuple3("isObject - not string", testInstance.isObject(booleanIdx), false),
            Tuple3("isObject - not found", testInstance.isObject(doesNotExistIdx), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isString() = listOf(
            Tuple3("isString", testInstance.isString(stringIdx), true),
            Tuple3("isString - not string", testInstance.isString(booleanIdx), false),
            Tuple3("isString - not found", testInstance.isString(doesNotExistIdx), false),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun isPrimitive() = listOf(
            Tuple3("isPrimitive", testInstance.isPrimitive(stringIdx), true),
            Tuple3("isPrimitive - not terminal", testInstance.isPrimitive(arrayIdx), false),
            Tuple3("isPrimitive - not found", testInstance.isPrimitive(doesNotExistIdx), false),
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
            Tuple3("asArray", testInstance.asArray(arrayIdx), JsonArray.builder().append(42).build()),
            Tuple3("asBoolean", testInstance.asBoolean(booleanIdx), JsonBoolean(true)),
            Tuple3("asNumber", testInstance.asNumber(numberIdx), JsonNumber(123)),
            Tuple3("asNull", testInstance.asNull(nullIdx), JsonNull),
            Tuple3("asNull - does not found", testInstance.asNull(doesNotExistIdx), JsonNull),
            Tuple3("asObject", testInstance.asObject(objectIdx), JsonObject.builder().set("hamal", "rocks").build()),
            Tuple3("asString", testInstance.asString(stringIdx), JsonString("SomeString")),
            Tuple3("asPrimitive", testInstance.asPrimitive(stringIdx), JsonString("SomeString")),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun `as - not found`() = listOf<Tuple3<kotlin.String, (Int) -> Any, kotlin.String>>(
            Tuple3("asArray", testInstance::asArray, "Not JsonArray"),
            Tuple3("asBoolean", testInstance::asBoolean, "Not JsonBoolean"),
            Tuple3("asNumber", testInstance::asNumber, "Not JsonNumber"),
            Tuple3("asObject", testInstance::asObject, "Not JsonObject"),
            Tuple3("asString", testInstance::asString, "Not JsonString"),
            Tuple3("asPrimitive", testInstance::asPrimitive, "Not JsonPrimitive"),
        ).map { (testName, fn, expectedErrorMessage) ->
            dynamicTest(testName) {
                assertThrows<IllegalStateException> {
                    fn(doesNotExistIdx)
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }

        @TestFactory
        fun `as - wrong type`() = listOf<Tuple4<kotlin.String, Int, (Int) -> Any, kotlin.String>>(
            Tuple4("asArray", objectIdx, testInstance::asArray, "Not JsonArray"),
            Tuple4("asBoolean", stringIdx, testInstance::asBoolean, "Not JsonBoolean"),
            Tuple4("asNumber", booleanIdx, testInstance::asNumber, "Not JsonNumber"),
            Tuple4("asObject", arrayIdx, testInstance::asObject, "Not JsonObject"),
            Tuple4("asString", numberIdx, testInstance::asString, "Not JsonString"),
            Tuple4("asPrimitive", arrayIdx, testInstance::asPrimitive, "Not JsonPrimitive"),
        ).map { (testName, idx, fn, expectedErrorMessage) ->
            dynamicTest(testName) {
                assertThrows<IllegalStateException> {
                    fn(idx)
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
            Tuple3("booleanValue", testInstance.booleanValue(booleanIdx), true),
            Tuple3("byteValue", testInstance.byteValue(numberIdx), (123).toByte()),
            Tuple3("decimalValue", testInstance.decimalValue(numberIdx), Decimal(123)),
            Tuple3("doubleValue", testInstance.doubleValue(numberIdx), 123.0),
            Tuple3("floatValue", testInstance.floatValue(numberIdx), 123.0f),
            Tuple3("intValue", testInstance.intValue(numberIdx), 123),
            Tuple3("longValue", testInstance.longValue(numberIdx), 123L),
            Tuple3("shortValue", testInstance.shortValue(numberIdx), (123).toShort()),
            Tuple3("stringValue", testInstance.stringValue(stringIdx), "SomeString")
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun `value - not found`() = listOf<Tuple3<kotlin.String, (Int) -> Any, kotlin.String>>(
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
                    fn(doesNotExistIdx)
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }

        @TestFactory
        fun `value - wrong type`() = listOf<Tuple4<kotlin.String, Int, (Int) -> Any, kotlin.String>>(
            Tuple4("booleanValue", stringIdx, testInstance::booleanValue, "Not JsonBoolean"),
            Tuple4("byteValue", booleanIdx, testInstance::byteValue, "Not JsonNumber"),
            Tuple4("decimalValue", booleanIdx, testInstance::decimalValue, "Not JsonNumber"),
            Tuple4("doubleValue", booleanIdx, testInstance::doubleValue, "Not JsonNumber"),
            Tuple4("floatValue", booleanIdx, testInstance::floatValue, "Not JsonNumber"),
            Tuple4("intValue", booleanIdx, testInstance::intValue, "Not JsonNumber"),
            Tuple4("longValue", booleanIdx, testInstance::longValue, "Not JsonNumber"),
            Tuple4("shortValue", booleanIdx, testInstance::shortValue, "Not JsonNumber"),
            Tuple4("stringValue", numberIdx, testInstance::stringValue, "Not JsonString")
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
        val testInstance = JsonArray.empty
        assertThat(testInstance.size, equalTo(0))
    }

    @Nested
    inner class FindTest {

        @Test
        fun `Finds key`() {
            val result = testInstance.find(0)
            assertThat(result, equalTo(JsonString("A")))
        }

        @Test
        fun `Does not find key`() {
            val result = testInstance.find(1)
            assertThat(result, nullValue())
        }

        private val testInstance: JsonArray = JsonArray.builder().append("A").build()
    }

    @Nested
    inner class GetTest {

        @Test
        fun `Gets key`() {
            val result = testInstance.get(0)
            assertThat(result, equalTo(JsonString("A")))
        }

        @Test
        fun `Does not get key`() {
            assertThrows<NoSuchElementException> {
                testInstance.get(1)
            }.let { exception ->
                assertThat(exception.message, equalTo("Element at index 1 not found"))
            }
        }

        private val testInstance: JsonArray = JsonArray.builder().append("A").build()
    }

    @Nested
    inner class DeepCopyTest {

        @Test
        fun empty() {
            val testInstance = JsonArray.empty
            val result = testInstance.deepCopy()

            assertTrue(testInstance == result)
            assertThat(result.size, equalTo(0))
        }

        @Test
        fun `Copy object`() {
            val result = testInstance.deepCopy()
            assertTrue(testInstance == result)
            assertThat(result.size, equalTo(6))

            assertThat(result.asArray(arrayIdx), equalTo(JsonArray.builder().append(42).build()))
            assertThat(result.asBoolean(booleanIdx), equalTo(JsonBoolean(true)))
            assertThat(result.asNumber(numberIdx), equalTo(JsonNumber(123)))
            assertThat(result.asString(stringIdx), equalTo(JsonString("SomeString")))
            assertThat(result.asObject(objectIdx), equalTo(JsonObject.builder().set("hamal", "rocks").build()))
            assertThat(result.asNull(nullIdx), equalTo(JsonNull))
        }
    }

}

private val testInstance = JsonArray.builder()
    .append(JsonArray.builder().append(42).build())
    .append(true)
    .append(123)
    .append("SomeString")
    .append(JsonObject.builder().set("hamal", "rocks").build())
    .appendNull()
    .build()

private const val arrayIdx = 0
private const val booleanIdx = 1
private const val numberIdx = 2
private const val stringIdx = 3
private const val objectIdx = 4
private const val nullIdx = 5

private const val doesNotExistIdx = 100
