package io.hamal.lib.common.hot

import io.hamal.lib.common.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows


internal object HotBooleanTest {

    @TestFactory
    fun isTest() = HotBoolean(true).let { testInstance ->
        listOf(
            Tuple3("isArray", testInstance::isArray, false),
            Tuple3("isBoolean", testInstance::isBoolean, true),
            Tuple3("isNumber", testInstance::isNumber, false),
            Tuple3("isNull", testInstance::isNull, false),
            Tuple3("isObject", testInstance::isObject, false),
            Tuple3("isString", testInstance::isText, false),
            Tuple3("isTerminal", testInstance::isTerminal, true)
        )
    }.map { (testName, func, expected) ->
        dynamicTest(testName) {
            assertThat(func(), equalTo(expected))
        }
    }

    @TestFactory
    fun `as`() = HotBoolean(true).let { testInstance ->
        listOf(
            Tuple3("asBoolean - true", HotBoolean(true)::asBoolean, HotBoolean(true)),
            Tuple3("asBoolean - false", HotBoolean(false)::asBoolean, HotBoolean(false)),
//            Tuple3("isNumber", testInstance.isNumber, false),
//            Tuple3("isNull", testInstance.isNull, false),
//            Tuple3("isObject", testInstance.isObject, false),
//            Tuple3("isString", testInstance.isString, false),
//            Tuple3("isTerminal", testInstance.isTerminal, true)
        )
    }.map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }

    @TestFactory
    fun `as invalid`() = HotBoolean(true).let { testInstance ->
        listOf(
            Tuple3("asArray", testInstance::asArray, "Not HotArray"),
            Tuple3("asTerminal", testInstance::asTerminal, "Not HotTerminal"),
//            Tuple3("isNumber", testInstance.isNumber, false),
//            Tuple3("isNull", testInstance.isNull, false),
//            Tuple3("isObject", testInstance.isObject, false),
//            Tuple3("isString", testInstance.isString, false),
//            Tuple3("isTerminal", testInstance.isTerminal, true)
        )
    }.map { (testName, func, expectedMessage) ->
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
    fun `value`() = HotBoolean(true).let { testInstance ->
        listOf(
            Tuple3("booleanValue", testInstance::booleanValue, true),
//            Tuple3("isNumber", testInstance.isNumber, false),
//            Tuple3("isNull", testInstance.isNull, false),
//            Tuple3("isObject", testInstance.isObject, false),
//            Tuple3("isString", testInstance.isString, false),
//            Tuple3("isTerminal", testInstance.isTerminal, true)
        )
    }.map { (testName, func, expected) ->
        dynamicTest(testName) {
            val result = func()
            assertThat(result, equalTo(expected))
        }
    }


//
//    @Nested
//    @DisplayName("deepCopy()")
//    internal class DeepCopyTest {
//        @Test
//        @DisplayName("ok")
//        fun ok() {
//            val testInstance: HotBooleanTerminal = HotBooleanTerminal(false)
//            val result: Unit = testInstance.deepCopy()
//            assertThat(result.checksum(), CoreMatchers.`is`(testInstance.checksum))
//
//            Assertions.assertSame(testInstance, result)
//        }
//    }
}