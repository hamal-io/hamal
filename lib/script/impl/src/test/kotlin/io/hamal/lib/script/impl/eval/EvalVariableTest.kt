package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.NilValue
import io.hamal.lib.kua.value.NumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class EvalVariableTest : AbstractEvalTest() {
    @TestFactory
    fun evalVariableTest() = prepareTests(listOf(
        """some_number=2810""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["some_number"], equalTo(NumberValue(2810)))
        },
        """x,y,z = 1,2,3""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["x"], equalTo(NumberValue(1)))
            assertThat(env["y"], equalTo(NumberValue(2)))
            assertThat(env["z"], equalTo(NumberValue(3)))
        },
        """local x = nil""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["x"], equalTo(NilValue))
        },
        """local some_local_number = 1212""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["some_local_number"], equalTo(NumberValue(1212)))
        },
        """local x,y,z = 1,2,3""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["x"], equalTo(NumberValue(1)))
            assertThat(env["y"], equalTo(NumberValue(2)))
            assertThat(env["z"], equalTo(NumberValue(3)))

        }
    ))
}