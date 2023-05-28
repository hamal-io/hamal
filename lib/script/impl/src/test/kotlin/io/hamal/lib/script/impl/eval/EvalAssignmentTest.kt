package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalAssignmentTest : AbstractEvalTest() {

    @TestFactory
    fun globalTests() = prepareTests(
        listOf(
            """x = 1; y = x""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["x"], equalTo(DepNumberValue(1)))
                assertThat(env["y"], equalTo(DepNumberValue(1)))
            },
            """y=0; x = 1; do y = x end""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["x"], equalTo(DepNumberValue(1)))
                assertThat(env["y"], equalTo(DepNumberValue(1)))
            },
        )
    )

    @TestFactory
    fun localTests() = prepareTests(
        listOf(
            """local birthday = 2810""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["birthday"], equalTo(DepNumberValue(2810)))
            },
            """local project_name = 'Hamal'""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["project_name"], equalTo(DepStringValue("Hamal")))
            },
            """local isFun = true""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["isFun"], equalTo(DepTrueValue))
            },
            """local isFailure = false""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["isFailure"], equalTo(DepFalseValue))
            },
            """local value = nil""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["value"], equalTo(DepNilValue))
            },
            """local x = 1; local y = x""" to { result, env ->
                assertThat(result, equalTo(DepNilValue))
                assertThat(env["x"], equalTo(DepNumberValue(1)))
                assertThat(env["y"], equalTo(DepNumberValue(1)))
            },
        )
    )
}