package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class FunctionTest : AbstractAstTest() {
    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `Parse empty function`() {
            val result = parseExpression(
                Function.Parse,
                """
                function empty() 
                end
                """.trimIndent()
            ) as Function
            assertThat(result.identifier, equalTo(Identifier("empty")))
            assertThat(result.parameters, hasSize(0))
            assertThat(result.block, hasSize(0))
        }

        @Test
        fun `Parse empty function with single argument`() {
            val result = parseExpression(
                Function.Parse,
                """
                function empty_with_single_param(param_one) end
                """.trimIndent()
            ) as Function
            assertThat(result.identifier, equalTo(Identifier("empty_with_single_param")))
            assertThat(result.parameters, equalTo(listOf(Identifier("param_one"))))
            assertThat(result.block, hasSize(0))
        }

        @Test
        fun `Parse empty function with multiple arguments`() {
            val result = parseExpression(
                Function.Parse,
                """
                function empty_with_params(one,two,three) end
                """.trimIndent()
            ) as Function
            assertThat(result.identifier, equalTo(Identifier("empty_with_params")))
            assertThat(
                result.parameters, equalTo(
                    listOf(
                        Identifier("one"),
                        Identifier("two"),
                        Identifier("three")
                    )
                )
            )
            assertThat(result.block, hasSize(0))
        }
    }
}