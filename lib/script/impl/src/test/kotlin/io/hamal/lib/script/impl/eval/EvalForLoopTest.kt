package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepNumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class ForLoopTest : AbstractEvalTest() {
    @TestFactory
    fun tests() = prepareTests(
        listOf(
            """y = 0; for i=1,10 do y = i end""" to { result, env ->
                assertThat(result, equalTo(DepNumberValue(10)))
                assertThat(env["y"], equalTo(DepNumberValue(10)))
            },
            """y = 0; for i=0,10,5 do y = i end""" to { result, env ->
                assertThat(result, equalTo(DepNumberValue(10)))
                assertThat(env["y"], equalTo(DepNumberValue(10)))
            },
            """y = 0; for i=1,30,6 do y = i end""" to { result, env ->
                assertThat(result, equalTo(DepNumberValue(25)))
                assertThat(env["y"], equalTo(DepNumberValue(25)))
            },
            """y = 10; for i=10,1,-1 do y = i end""" to { result, env ->
                assertThat(result, equalTo(DepNumberValue(1)))
                assertThat(env["y"], equalTo(DepNumberValue(1)))
            },
        )
    )
}