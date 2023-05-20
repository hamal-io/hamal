package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.FalseValue
import io.hamal.lib.script.api.value.NilValue
import io.hamal.lib.script.api.value.NumberValue
import io.hamal.lib.script.api.value.TrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class EvalConditionTest : AbstractEvalTest() {

    @TestFactory
    fun tests() = prepareTests(listOf(
        """if  true then 42 end""" to { result, _ ->
            assertThat(result, equalTo(NumberValue(42)))
        },
        """if  false then 42 end""" to { result, _ ->
            assertThat(result, equalTo(NilValue))
        },
        """if true then a = 1337 end""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["a"], equalTo(NumberValue(1337)))
        },
        """if false then a = 1337 end""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["a"], equalTo(NilValue))
        },
        """if 1 == 1 then a = 42 end""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["a"], equalTo(NumberValue(42)))
        },
        """if 1 > 1 then a = 42 end""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(env["a"], equalTo(NilValue))
        },
        """if false then true else false end""" to { result, _ ->
            assertThat(result, equalTo(FalseValue))
        },
        """if true then true else false end""" to { result, _ ->
            assertThat(result, equalTo(TrueValue))
        }
    ))

}