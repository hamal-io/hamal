package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalTableTest : AbstractEvalTest() {

    @TestFactory
    fun evalTableTests() = prepareTests(listOf(
        """{ }""" to { result, _ ->
            assertThat(result, equalTo(DepTableValue()))
        },
        """{1212}""" to { result, _ ->
            assertThat(
                result, equalTo(
                    DepTableValue(
                        DepNumberValue(1) to DepNumberValue(1212)
                    )
                )
            )
        },
        """{true, 1, 2, 'some-str', false, nil}""" to { result, _ ->
            assertThat(
                result, equalTo(
                    DepTableValue(
                        DepNumberValue(1) to DepTrueValue,
                        DepNumberValue(2) to DepNumberValue(1),
                        DepNumberValue(3) to DepNumberValue(2),
                        DepNumberValue(4) to DepStringValue("some-str"),
                        DepNumberValue(5) to DepFalseValue,
                        DepNumberValue(6) to DepNilValue
                    )
                )
            )
        },
        """{some = 1212}""" to { result, _ ->
            assertThat(
                result, equalTo(
                    DepTableValue(
                        DepIdentifier("some") to DepNumberValue(1212)
                    )
                )
            )
        },
        """local t = {some = 1212}""" to { result, env ->
            assertThat(result, equalTo(DepNilValue))
            assertThat(
                env["t"], equalTo(
                    DepTableValue(
                        DepIdentifier("some") to DepNumberValue(1212)
                    )
                )
            )
        },

        """
            local t = {some = 1212}
            t['some']
            """.trimIndent() to { result, _ ->
            assertThat(result, equalTo(DepNumberValue(1212)))
        }

    ))
}