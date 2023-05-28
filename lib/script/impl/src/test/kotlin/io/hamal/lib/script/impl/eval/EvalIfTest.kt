package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.DepFalseValue
import io.hamal.lib.script.api.value.DepNilValue
import io.hamal.lib.script.api.value.DepNumberValue
import io.hamal.lib.script.api.value.DepTrueValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class EvalIfTest : AbstractEvalTest() {

    @TestFactory
    fun tests() = prepareTests(listOf(
        """if  true then 42 end""" to { result, _ ->
            assertThat(result, equalTo(DepNumberValue(42)))
        },
        """if  false then 42 end""" to { result, _ ->
            assertThat(result, equalTo(DepNilValue))
        },
        """if true then a = 1337 end""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["a"], equalTo(DepNumberValue(1337)))
        },
        """if false then a = 1337 end""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["a"], equalTo(DepNilValue))
        },
        """if 1 == 1 then a = 42 end""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["a"], equalTo(DepNumberValue(42)))
        },
        """if 1 > 1 then a = 42 end""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(env["a"], equalTo(DepNilValue))
        },
        """if false then true else false end""" to { result, _ ->
            assertThat(result, equalTo(DepFalseValue))
        },
        """if true then true else false end""" to { result, _ ->
            assertThat(result, equalTo(DepTrueValue))
        },
        """if false then 1 elseif true then 2 end""" to { result, _ ->
            assertThat(result, equalTo(DepNumberValue(2)))
        },
        """if false then 1 elseif false then 2 else 3 end""" to { result, _ ->
            assertThat(result, equalTo(DepNumberValue(3)))
        },
        """if false then 1 elseif false then 2 end""" to { result, _ ->
            assertThat(result, equalTo(DepNilValue))
        }
    ))
}

