package io.hamal.lib.script.impl.evaluation

import io.hamal.lib.script.api.value.NumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class NumberTest : AbstractEvalTest() {

    @Test
    fun `The universal answer`() {
        val result = eval("42")
        assertThat(result, equalTo(NumberValue(42)))
    }

    @ParameterizedTest(name = "#{index} - Test expression {0}")
    @MethodSource("testCases")
    fun `Parameterized tests`(code: String, expected: NumberValue) {
        val result = eval(code)
        assertThat(result, equalTo(expected));
    }

    companion object {
        @JvmStatic
        private fun testCases(): List<Arguments> {
            return listOf(
                Arguments.of("1 + 2", NumberValue(3)),
                Arguments.of("2 - 1", NumberValue(1)),
                Arguments.of("-2810", NumberValue(-2810)),
                Arguments.of("-(-1)", NumberValue(1))
            )
        }
    }

}