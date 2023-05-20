package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalAssignmentTest : AbstractEvalTest() {

    @TestFactory
    fun localTests() = prepareTests(listOf(
        """local birthday = 2810""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["birthday"], equalTo(NumberValue(2810)))
        },
        """local project_name = 'Hamal'""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["project_name"], equalTo(StringValue("Hamal")))
        },
        """local isFun = true""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["isFun"], equalTo(TrueValue))
        },
        """local isFailure = false""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["isFailure"], equalTo(FalseValue))
        },
        """local value = nil""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["value"], equalTo(NilValue))

        }
    ))
}