package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.ExpressionStatement
import io.hamal.module.worker.script.ast.stmt.Block
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class FunctionTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equals if identifier and parameters are equal`() {
            assertEquals(
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ),
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                )
            )
        }

        @Test
        fun `Not equals if identifier is not equal`() {
            assertNotEquals(
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ),
                Function(
                    Identifier("AnotherFunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                )
            )
        }

        @Test
        fun `Not equals if parameters are not equal`(){
            assertNotEquals(
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ),
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier"), Identifier("AnotherParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                )
            )
        }

    }

    @Nested
    @DisplayName("hashCode()")
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if identifier and parameters hashcode are the same`() {
            assertEquals(
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ).hashCode(),
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                ).hashCode()
            )
        }

        @Test
        fun`Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ).hashCode(),
                Function(
                    Identifier("AnotherFunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`(){
            assertNotEquals(
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ).hashCode(),
                Function(
                    Identifier("FunctionIdentifier"),
                    listOf(Identifier("FunctionParameterIdentifier"), Identifier("AnotherParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                ).hashCode()
            )
        }
    }

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