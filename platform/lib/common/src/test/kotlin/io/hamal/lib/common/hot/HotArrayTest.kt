package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import io.hamal.lib.common.Tuple4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger

internal class HotArrayTest {

    @TestFactory
    fun `is`() = listOf(
        Tuple3("isArray", HotArray.empty.isArray, true),
        Tuple3("isBoolean", HotArray.empty.isBoolean, false),
        Tuple3("isNumber", HotArray.empty.isNumber, false),
        Tuple3("isNull", HotArray.empty.isNull, false),
        Tuple3("isObject", HotArray.empty.isObject, false),
        Tuple3("isString", HotArray.empty.isString, false),
        Tuple3("isTerminal", HotArray.empty.isTerminal, false)
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
        fun isTerminal() = listOf(
            Tuple3("isTerminal", testInstance.isTerminal(stringIdx), true),
            Tuple3("isTerminal - not terminal", testInstance.isTerminal(arrayIdx), false),
            Tuple3("isTerminal - not found", testInstance.isTerminal(doesNotExistIdx), false),
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
            Tuple3("asArray", testInstance.asArray(arrayIdx), HotArray.builder().append(42).build()),
            Tuple3("asBoolean", testInstance.asBoolean(booleanIdx), HotBoolean(true)),
            Tuple3("asNumber", testInstance.asNumber(numberIdx), HotNumber(123)),
            Tuple3("asNull", testInstance.asNull(nullIdx), HotNull),
            Tuple3("asNull - does not found", testInstance.asNull(doesNotExistIdx), HotNull),
            Tuple3("asObject", testInstance.asObject(objectIdx), HotObject.builder().set("hamal", "rocks").build()),
            Tuple3("asString", testInstance.asString(stringIdx), HotString("SomeString")),
            Tuple3("asTerminal", testInstance.asTerminal(stringIdx), HotString("SomeString")),
        ).map { (testName, result, expected) ->
            dynamicTest(testName) {
                assertThat(result, equalTo(expected))
            }
        }

        @TestFactory
        fun `as - not found`() = listOf<Tuple3<String, (Int) -> Any, String>>(
            Tuple3("asArray", testInstance::asArray, "Not HotArray"),
            Tuple3("asBoolean", testInstance::asBoolean, "Not HotBoolean"),
            Tuple3("asNumber", testInstance::asNumber, "Not HotNumber"),
            Tuple3("asObject", testInstance::asObject, "Not HotObject"),
            Tuple3("asString", testInstance::asString, "Not HotString"),
            Tuple3("asTerminal", testInstance::asTerminal, "Not HotTerminal"),
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
        fun `as - wrong type`() = listOf<Tuple4<String, Int, (Int) -> Any, String>>(
            Tuple4("asArray", objectIdx, testInstance::asArray, "Not HotArray"),
            Tuple4("asBoolean", stringIdx, testInstance::asBoolean, "Not HotBoolean"),
            Tuple4("asNumber", booleanIdx, testInstance::asNumber, "Not HotNumber"),
            Tuple4("asObject", arrayIdx, testInstance::asObject, "Not HotObject"),
            Tuple4("asString", numberIdx, testInstance::asString, "Not HotString"),
            Tuple4("asTerminal", arrayIdx, testInstance::asTerminal, "Not HotTerminal"),
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
            Tuple3("bigDecimalValue", testInstance.bigDecimalValue(numberIdx), BigDecimal(123)),
            Tuple3("bigIntegerValue", testInstance.bigIntegerValue(numberIdx), BigInteger.valueOf(123)),
            Tuple3("byteValue", testInstance.byteValue(numberIdx), (123).toByte()),
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
        fun `value - not found`() = listOf<Tuple3<String, (Int) -> Any, String>>(
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
                    fn(doesNotExistIdx)
                }.let { exception ->
                    assertThat(exception.message, equalTo(expectedErrorMessage))
                }
            }
        }

        @TestFactory
        fun `value - wrong type`() = listOf<Tuple4<String, Int, (Int) -> Any, String>>(
            Tuple4("booleanValue", stringIdx, testInstance::booleanValue, "Not HotBoolean"),
            Tuple4("bigDecimalValue", booleanIdx, testInstance::bigDecimalValue, "Not HotNumber"),
            Tuple4("bigIntegerValue", booleanIdx, testInstance::bigIntegerValue, "Not HotNumber"),
            Tuple4("byteValue", booleanIdx, testInstance::byteValue, "Not HotNumber"),
            Tuple4("doubleValue", booleanIdx, testInstance::doubleValue, "Not HotNumber"),
            Tuple4("floatValue", booleanIdx, testInstance::floatValue, "Not HotNumber"),
            Tuple4("intValue", booleanIdx, testInstance::intValue, "Not HotNumber"),
            Tuple4("longValue", booleanIdx, testInstance::longValue, "Not HotNumber"),
            Tuple4("shortValue", booleanIdx, testInstance::shortValue, "Not HotNumber"),
            Tuple4("stringValue", numberIdx, testInstance::stringValue, "Not HotString")
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
        val testInstance = HotArray.empty
        assertThat(testInstance.size, equalTo(0))
    }

    @Nested
    inner class FindTest {

        @Test
        fun `Finds key`() {
            val result = testInstance.find(0)
            assertThat(result, equalTo(HotString("A")))
        }

        @Test
        fun `Does not find key`() {
            val result = testInstance.find(1)
            assertThat(result, nullValue())
        }

        private val testInstance: HotArray = HotArray.builder().append("A").build()
    }

    @Nested
    inner class GetTest {

        @Test
        fun `Gets key`() {
            val result = testInstance.get(0)
            assertThat(result, equalTo(HotString("A")))
        }

        @Test
        fun `Does not get key`() {
            assertThrows<NoSuchElementException> {
                testInstance.get(1)
            }.let { exception ->
                assertThat(exception.message, equalTo("Element at index 1 not found"))
            }
        }

        private val testInstance: HotArray = HotArray.builder().append("A").build()
    }

    @Nested
    inner class DeepCopyTest {

        @Test
        fun empty() {
            val testInstance = HotArray.empty
            val result = testInstance.deepCopy()

            assertFalse(testInstance === result)
            require(result is HotArray)
            assertThat(result.size, equalTo(0))
        }

        @Test
        fun `Copy object`() {
            val result = testInstance.deepCopy()
            assertFalse(testInstance === result)
            require(result is HotArray)
            assertThat(result.size, equalTo(6))

            assertThat(result.asArray(arrayIdx), equalTo(HotArray.builder().append(42).build()))
            assertThat(result.asBoolean(booleanIdx), equalTo(HotBoolean(true)))
            assertThat(result.asNumber(numberIdx), equalTo(HotNumber(123)))
            assertThat(result.asString(stringIdx), equalTo(HotString("SomeString")))
            assertThat(result.asObject(objectIdx), equalTo(HotObject.builder().set("hamal", "rocks").build()))
            assertThat(result.asNull(nullIdx), equalTo(HotNull))
        }
    }

}

private val testInstance = HotArray.builder()
    .append(HotArray.builder().append(42).build())
    .append(true)
    .append(123)
    .append("SomeString")
    .append(HotObject.builder().set("hamal", "rocks").build())
    .appendNull()
    .build()

private const val arrayIdx = 0
private const val booleanIdx = 1
private const val numberIdx = 2
private const val stringIdx = 3
private const val objectIdx = 4
private const val nullIdx = 5

private const val doesNotExistIdx = 100
