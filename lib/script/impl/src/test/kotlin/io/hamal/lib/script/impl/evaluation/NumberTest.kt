package io.hamal.lib.script.impl.evaluation

import io.hamal.lib.script.api.value.NumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class NumberTest : AbstractEvalTest() {

    @Test
    fun `The universal answer`() {
        val result = eval("42")
        assertThat(result, equalTo(NumberValue(42)))
    }

    @TestFactory
    fun numberTests() = listOf(
        Pair("1 + 2", NumberValue(3)),
        Pair("2 - 1", NumberValue(1)),
        Pair("-2810", NumberValue(-2810)),
        Pair("-(-1)", NumberValue(1))
    ).map { (code, expected) ->
        dynamicTest("Test expression $code") {
            val result = eval(code)
            assertThat(result, equalTo(expected))
        }
    }
}