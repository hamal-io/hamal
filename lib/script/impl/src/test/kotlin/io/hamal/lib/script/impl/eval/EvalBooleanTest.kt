package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.FalseValue
import io.hamal.lib.kua.value.TrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class EvalBooleanTest : AbstractEvalTest() {

    @TestFactory
    fun tests() = prepareTests(listOf(
        """true""" to { result, _ ->
            assertThat(result, equalTo(TrueValue))
        },
        """false""" to { result, _ ->
            assertThat(result, equalTo(FalseValue))
        }
    ))
}