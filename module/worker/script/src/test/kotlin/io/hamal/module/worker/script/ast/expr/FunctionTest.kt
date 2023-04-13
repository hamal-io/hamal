package io.hamal.module.worker.script.ast.expr

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class FunctionTest : AbstractExpressionTest() {
    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `Parse empty function`() {
            runLiteralTest(
                Function.Parse,
                """
                function empty() 
                end
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.identifier, equalTo(Identifier("empty")))
                assertThat(result.parameters, hasSize(0))
                assertThat(result.block, hasSize(0))

                tokens.wereConsumed()
            }
        }

        @Test
        fun `Parse empty function with single argument`() {
            runLiteralTest(
                Function.Parse,
                """
                function empty_with_single_param(param_one) end
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.identifier, equalTo(Identifier("empty_with_single_param")))
                assertThat(result.parameters, equalTo(listOf(Identifier("param_one"))))
                assertThat(result.block, hasSize(0))

                tokens.wereConsumed()
            }
        }

        @Test
        fun `Parse empty function with multiple arguments`() {
            runLiteralTest(
                Function.Parse,
                """
                function empty_with_params(one,two,three) end
                """.trimIndent()
            ) { result, tokens ->
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

                tokens.wereConsumed()
            }
        }
    }
}