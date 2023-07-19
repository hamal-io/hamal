package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.NumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class ForLoopTest : AbstractEvalTest() {
    @TestFactory
    fun tests() = prepareTests(
        listOf(
            """y = 0; for i=1,10 do y = i end""" to { result, env ->
                assertThat(result, equalTo(NumberValue(10)))
                assertThat(env["y"], equalTo(NumberValue(10)))
            },
            """y = 0; for i=0,10,5 do y = i end""" to { result, env ->
                assertThat(result, equalTo(NumberValue(10)))
                assertThat(env["y"], equalTo(NumberValue(10)))
            },
            """y = 0; for i=1,30,6 do y = i end""" to { result, env ->
                assertThat(result, equalTo(NumberValue(25)))
                assertThat(env["y"], equalTo(NumberValue(25)))
            },
            """y = 10; for i=10,1,-1 do y = i end""" to { result, env ->
                assertThat(result, equalTo(NumberValue(1)))
                assertThat(env["y"], equalTo(NumberValue(1)))
            },
        )
    )
}