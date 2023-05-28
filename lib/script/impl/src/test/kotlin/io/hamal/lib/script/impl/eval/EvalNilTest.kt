package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepFalseValue
import io.hamal.lib.script.api.value.DepTrueValue
import org.junit.jupiter.api.TestFactory

internal class EvalNilTest : AbstractEvalTest() {
    @TestFactory
    fun nilTests() = prepareTests(
        listOf(
            """nil == nil""" to equalToValue(DepTrueValue),
            """nil ~= nil""" to equalToValue(DepFalseValue)
        )
    )
}