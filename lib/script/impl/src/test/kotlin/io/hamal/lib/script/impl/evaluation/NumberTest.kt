package io.hamal.lib.script.impl.evaluation

import io.hamal.lib.script.api.value.FalseValue
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.TrueValue
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
        "1 + 2" to NumberValue(3),

        "2 - 1" to NumberValue(1),

        "2 * 2" to NumberValue(4),

        "4 / 2" to NumberValue(2),

        "3 < 2" to FalseValue,
        "2 < 2" to FalseValue,
        "1 < 2" to TrueValue,

        "3 <= 2" to FalseValue,
        "2 <= 2" to TrueValue,
        "1 <= 2" to TrueValue,

        "3 > 2" to TrueValue,
        "2 > 2" to FalseValue,
        "1 > 2" to FalseValue,

        "3 >= 2" to TrueValue,
        "2 >= 2" to TrueValue,
        "1 >= 2" to FalseValue,

        "10 % 5" to NumberValue(0),
        "2^10" to NumberValue(1024),

        "1 == 2" to FalseValue,
        "2 == 2" to TrueValue,
        "2 == 1" to FalseValue,

        "-2810" to NumberValue(-2810),
        "-(-1)" to NumberValue(1)
    ).map { (code, expected) ->
        dynamicTest("Test expression $code") {
            val result = eval(code)
            assertThat(result, equalTo(expected))
        }
    }
}