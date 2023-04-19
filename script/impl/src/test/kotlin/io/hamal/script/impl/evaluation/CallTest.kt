package io.hamal.script.impl.evaluation

import io.hamal.script.api.value.NumberValue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class CallTest : AbstractEvalTest() {

    @Test
    fun `Function which returns the answer - but what was the question`() {
        val result = eval(
            """
            function answer() return 42  end
            answer()
        """.trimIndent()
        )
        assertThat(result, equalTo(NumberValue(42)))
    }


    @Test
    fun `Function which returns the answer twice- but what was the question`() {
        val result = eval(
            """
            function answer()
                return 42
            end
            
            answer()
            answer()
        """.trimIndent()
        )
        assertThat(result, equalTo(NumberValue(42)))
    }
}