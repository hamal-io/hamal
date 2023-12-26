package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
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

//    @TestFactory
//    fun `is with key`() = listOf(
////        Tuple3("isArray", HotObject.empty.isArray, false),
//        Tuple3("isBoolean", HotObject.builder().set("key", true).build(), true),
//        Tuple3("isBoolean - not boolean", HotObject.builder().set("key", 123).build(), false),
//        Tuple3("isBoolean - not found", HotObject.builder().set("key", 123).build(), false),
////        Tuple3("isNumber", HotObject.empty.isNumber, false),
////        Tuple3("isNull", HotObject.empty.isNull, false),
////        Tuple3("isObject", HotObject.empty.isObject, true),
////        Tuple3("isString", HotObject.empty.isString, false),
////        Tuple3("isTerminal", HotObject.empty.isTerminal, false)
//    ).map { (testName, testInstance, expected) ->
//        dynamicTest(testName) {
//            assertThat(testInstance., equalTo(expected))
//        }
//    }

    internal class IsTest {

        @TestFactory
        fun isArray() = listOf(
            Tuple3("isArray", testInstance.isArray("array"), true),
            Tuple3("isArray - not string", testInstance.isArray("boolean"), false),
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

        private val testInstance = HotObject.builder()
            .set("array", HotArray.builder().add(42).build())
            .set("boolean", true)
            .set("number", 123)
            .set("string", "SomeString")
            .set("object", HotObject.builder().set("hamal", "rocks").build())
            .setNull("null")
            .build()
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
        fun `Contains not key`() {
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
}