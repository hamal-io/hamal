package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.FalseValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.TrueValue
import org.junit.jupiter.api.TestFactory

internal class EvalNumberTest : AbstractEvalTest() {

    @TestFactory
    fun numberTests() = prepareTests(
        listOf(
            "42" to equalToValue(NumberValue(42)),
            "1 + 2" to equalToValue(NumberValue(3)),

            "2 - 1" to equalToValue(NumberValue(1)),

            "2 * 2" to equalToValue(NumberValue(4)),

            "4 / 2" to equalToValue(NumberValue(2)),

            "3 < 2" to equalToValue(FalseValue),
            "2 < 2" to equalToValue(FalseValue),
            "1 < 2" to equalToValue(TrueValue),

            "3 <= 2" to equalToValue(FalseValue),
            "2 <= 2" to equalToValue(TrueValue),
            "1 <= 2" to equalToValue(TrueValue),

            "3 > 2" to equalToValue(TrueValue),
            "2 > 2" to equalToValue(FalseValue),
            "1 > 2" to equalToValue(FalseValue),

            "3 >= 2" to equalToValue(TrueValue),
            "2 >= 2" to equalToValue(TrueValue),
            "1 >= 2" to equalToValue(FalseValue),

            "10 % 5" to equalToValue(NumberValue(0)),
            "2^10" to equalToValue(NumberValue(1024)),

            "1 == 2" to equalToValue(FalseValue),
            "2 == 2" to equalToValue(TrueValue),
            "2 == 1" to equalToValue(FalseValue),

            "-2810" to equalToValue(NumberValue(-2810)),
            "-(-1)" to equalToValue(NumberValue(1))
        )
    )
}