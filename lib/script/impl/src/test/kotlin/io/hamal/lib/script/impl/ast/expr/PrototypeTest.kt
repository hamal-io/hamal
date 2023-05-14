package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.stmt.BlockStatement
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PrototypeTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if identifier and parameters are equal`() {
            assertEquals(
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(TrueLiteral()))
                ),
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(FalseLiteral()))
                )
            )
        }

        @Test
        fun `Not Equal if identifiers are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(TrueLiteral()))
                ),
                PrototypeLiteral(
                    IdentifierExpression("AnotherIdentifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(FalseLiteral()))
                )
            )
        }

        @Test
        fun `Not Equal if parameters are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(TrueLiteral()))
                ),
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(
                        IdentifierExpression("ParameterIdentifier"),
                        IdentifierExpression("AnotherParameterIdentifier")
                    ),
                    BlockStatement(ExpressionStatement(FalseLiteral()))
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
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(TrueLiteral()))
                ).hashCode(),
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(FalseLiteral()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(TrueLiteral()))
                ).hashCode(),
                PrototypeLiteral(
                    IdentifierExpression("AnotherIdentifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(FalseLiteral()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                    BlockStatement(ExpressionStatement(TrueLiteral()))
                ).hashCode(),
                PrototypeLiteral(
                    IdentifierExpression("Identifier"),
                    listOf(
                        IdentifierExpression("ParameterIdentifier"),
                        IdentifierExpression("AnotherParameterIdentifier")
                    ),
                    BlockStatement(ExpressionStatement(FalseLiteral()))
                ).hashCode()
            )
        }
    }

    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `Parse empty `() {
            runLiteralTest(
                PrototypeLiteral.Parse,
                """
                function empty() 
                end
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.identifier, equalTo(IdentifierExpression("empty")))
                assertThat(result.parameters, hasSize(0))
                assertThat(result.block, hasSize(0))

                tokens.wereConsumed()
            }
        }

        @Test
        fun `Parse empty with single argument`() {
            runLiteralTest(
                PrototypeLiteral.Parse,
                """
                function empty_with_single_param(param_one) end
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.identifier, equalTo(IdentifierExpression("empty_with_single_param")))
                assertThat(result.parameters, equalTo(listOf(IdentifierExpression("param_one"))))
                assertThat(result.block, hasSize(0))

                tokens.wereConsumed()
            }
        }

        @Test
        fun `Parse empty with multiple arguments`() {
            runLiteralTest(
                PrototypeLiteral.Parse,
                """
                function empty_with_params(one,two,three) end
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.identifier, equalTo(IdentifierExpression("empty_with_params")))
                assertThat(
                    result.parameters, equalTo(
                        listOf(
                            IdentifierExpression("one"),
                            IdentifierExpression("two"),
                            IdentifierExpression("three")
                        )
                    )
                )
                assertThat(result.block, hasSize(0))

                tokens.wereConsumed()
            }
        }
    }
}