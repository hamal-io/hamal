package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepFalseValue
import io.hamal.lib.script.api.value.DepNumberValue
import io.hamal.lib.script.api.value.DepTrueValue
import org.junit.jupiter.api.TestFactory

internal class EvalNumberTest : AbstractEvalTest() {

    @TestFactory
    fun numberTests() = prepareTests(
        listOf(
            "42" to equalToValue(DepNumberValue(42)),
            "1 + 2" to equalToValue(DepNumberValue(3)),

            "2 - 1" to equalToValue(DepNumberValue(1)),

            "2 * 2" to equalToValue(DepNumberValue(4)),

            "4 / 2" to equalToValue(DepNumberValue(2)),

            "3 < 2" to equalToValue(DepFalseValue),
            "2 < 2" to equalToValue(DepFalseValue),
            "1 < 2" to equalToValue(DepTrueValue),

            "3 <= 2" to equalToValue(DepFalseValue),
            "2 <= 2" to equalToValue(DepTrueValue),
            "1 <= 2" to equalToValue(DepTrueValue),

            "3 > 2" to equalToValue(DepTrueValue),
            "2 > 2" to equalToValue(DepFalseValue),
            "1 > 2" to equalToValue(DepFalseValue),

            "3 >= 2" to equalToValue(DepTrueValue),
            "2 >= 2" to equalToValue(DepTrueValue),
            "1 >= 2" to equalToValue(DepFalseValue),

            "10 % 5" to equalToValue(DepNumberValue(0)),
            "2^10" to equalToValue(DepNumberValue(1024)),

            "1 == 2" to equalToValue(DepFalseValue),
            "2 == 2" to equalToValue(DepTrueValue),
            "2 == 1" to equalToValue(DepFalseValue),

            "-2810" to equalToValue(DepNumberValue(-2810)),
            "-(-1)" to equalToValue(DepNumberValue(1))
        )
    )
}