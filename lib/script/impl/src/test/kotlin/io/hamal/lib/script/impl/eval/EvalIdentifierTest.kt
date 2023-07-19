package io.hamal.lib.script.impl.eval

import io.hamal.lib.kua.value.NumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.TestFactory

internal class EvalIdentifierTest : AbstractEvalTest() {
    @TestFactory
    fun evalIdentifierTests() = prepareTests(listOf(
        """local birthday = 2810
                birthday
                """.trimIndent() to { result, _ ->
            assertThat(result, equalTo(NumberValue(2810)))
        }

    ))
}