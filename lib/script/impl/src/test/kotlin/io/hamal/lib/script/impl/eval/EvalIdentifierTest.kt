package io.hamal.lib.script.impl.eval

import io.hamal.lib.script.api.value.NumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class EvalIdentifierTest : AbstractEvalTest() {
    @Test
    fun `Evaluates local identifier which is a number`() {
        val result = eval(
            """
                local birthday = 2810
                birthday
        """.trimIndent()
        )
        assertThat(result, equalTo(NumberValue(2810)))
    }
}