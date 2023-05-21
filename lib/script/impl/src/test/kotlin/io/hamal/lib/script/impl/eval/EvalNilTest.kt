package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.FalseValue
import io.hamal.lib.script.api.value.TrueValue
import org.junit.jupiter.api.TestFactory

internal class EvalNilTest : AbstractEvalTest() {
    @TestFactory
    fun nilTests() = prepareTests(
        listOf(
            """nil == nil""" to equalToValue(TrueValue),
            """nil ~= nil""" to equalToValue(FalseValue)
        )
    )
}