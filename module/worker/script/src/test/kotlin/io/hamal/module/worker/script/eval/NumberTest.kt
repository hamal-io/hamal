package io.hamal.module.worker.script.eval

import io.hamal.lib.meta.Tuple2
import io.hamal.lib.meta.math.Decimal
import io.hamal.module.worker.script.value.NumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class NumberTest : AbstractEvalTest() {

    @Test
    fun `The universal answer`() {
        val result = eval("42")
        assertThat(result, equalTo(NumberValue(42)))
    }


    @ParameterizedTest(name = "#{index} - Test expression {0}")
    @MethodSource("testCases")
    fun test(arg: Tuple2<String, Decimal>) {
        val result = eval(arg._1)
        val expected = arg._2;
        assertThat(result, equalTo(expected));
    }

    companion object {
        @JvmStatic
        private fun testCases(): List<Tuple2<String, Decimal>> {
            return listOf(
                Tuple2("5 + 5", Decimal(10)),
            );
        }
    }

}