package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepFalseValue
import io.hamal.lib.script.api.value.DepTrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class EvalBooleanTest : AbstractEvalTest() {

    @TestFactory
    fun tests() = prepareTests(listOf(
        """true""" to { result, _ ->
            assertThat(result, equalTo(DepTrueValue))
        },
        """false""" to { result, _ ->
            assertThat(result, equalTo(DepFalseValue))
        }
    ))
}