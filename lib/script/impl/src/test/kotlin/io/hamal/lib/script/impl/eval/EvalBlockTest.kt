package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepNilValue
import io.hamal.lib.script.api.value.DepNumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalBlockTest : AbstractEvalTest() {
    @TestFactory
    fun blockTests() = prepareTests(
        listOf(
            """do end""" to { result, _ ->
                assertThat(result, equalTo(DepNilValue))
            },
            """x = 0; do x = 10 end""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["x"], equalTo(DepNumberValue(10)))
            },
        )
    )
}