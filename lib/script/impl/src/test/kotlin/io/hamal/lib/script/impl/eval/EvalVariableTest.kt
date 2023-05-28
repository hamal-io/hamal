package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepNilValue
import io.hamal.lib.script.api.value.DepNumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class EvalVariableTest : AbstractEvalTest() {
    @TestFactory
    fun evalVariableTest() = prepareTests(listOf(
        """some_number=2810""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["some_number"], equalTo(DepNumberValue(2810)))
        },
        """x,y,z = 1,2,3""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["x"], equalTo(DepNumberValue(1)))
            assertThat(env["y"], equalTo(DepNumberValue(2)))
            assertThat(env["z"], equalTo(DepNumberValue(3)))
        },
        """local x = nil""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["x"], equalTo(DepNilValue))
        },
        """local some_local_number = 1212""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["some_local_number"], equalTo(DepNumberValue(1212)))
        },
        """local x,y,z = 1,2,3""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["x"], equalTo(DepNumberValue(1)))
            assertThat(env["y"], equalTo(DepNumberValue(2)))
            assertThat(env["z"], equalTo(DepNumberValue(3)))

        }
    ))
}