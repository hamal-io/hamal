package io.hamal.lib.script.impl.eval

import io.hamal.lib.common.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalTableTest : AbstractEvalTest() {

    @TestFactory
    fun evalTableTests() = prepareTests(listOf(
        """{ }""" to { result, _ ->
            assertThat(result, equalTo(TableValue()))
        },
        """{1212}""" to { result, _ ->
            assertThat(
                result, equalTo(
                    TableValue(
                        NumberValue(1) to NumberValue(1212)
                    )
                )
            )
        },
        """{true, 1, 2, 'some-str', false, nil}""" to { result, _ ->
            assertThat(
                result, equalTo(
                    TableValue(
                        NumberValue(1) to TrueValue,
                        NumberValue(2) to NumberValue(1),
                        NumberValue(3) to NumberValue(2),
                        NumberValue(4) to StringValue("some-str"),
                        NumberValue(5) to FalseValue,
                        NumberValue(6) to NilValue
                    )
                )
            )
        },
        """{some = 1212}""" to { result, _ ->
            assertThat(
                result, equalTo(
                    TableValue(
                        IdentValue("some") to NumberValue(1212)
                    )
                )
            )
        },
        """local t = {some = 1212}""" to { result, env ->
            assertThat(result, equalTo(NilValue))
            assertThat(
                env["t"], equalTo(
                    TableValue(
                        IdentValue("some") to NumberValue(1212)
                    )
                )
            )
        },

        """
            local t = {some = 1212}
            t['some']
            """.trimIndent() to { result, _ ->
            assertThat(result, equalTo(NumberValue(1212)))
        }

    ))
}