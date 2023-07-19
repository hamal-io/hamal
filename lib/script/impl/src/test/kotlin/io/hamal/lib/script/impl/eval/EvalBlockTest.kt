package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.NilValue
import io.hamal.lib.kua.value.NumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalBlockTest : AbstractEvalTest() {
    @TestFactory
    fun blockTests() = prepareTests(
        listOf(
            """do end""" to { result, _ ->
                assertThat(result, equalTo(NilValue))
            },
            """x = 0; do x = 10 end""" to { result, env ->
                assertThat(result, equalTo(NilValue))
                assertThat(env["x"], equalTo(NumberValue(10)))
            },
        )
    )
}