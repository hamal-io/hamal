package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PrototypeLiteralTest : AbstractExpressionTest() {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if identifier and parameters are equal`() {
            assertEquals(
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral))
                ),
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral))
                )
            )
        }

        @Test
        fun `Not Equal if identifiers are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral))
                ),
                PrototypeLiteral(
                    IdentifierLiteral("AnotherIdentifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral))
                )
            )
        }

        @Test
        fun `Not Equal if parameters are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral))
                ),
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(
                        IdentifierLiteral("ParameterIdentifier"),
                        IdentifierLiteral("AnotherParameterIdentifier")
                    ),
                    Block(ExpressionStatement(FalseLiteral))
                )
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if identifier and parameters hashcode are the same`() {
            assertEquals(
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral))
                ).hashCode(),
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral))
                ).hashCode(),
                PrototypeLiteral(
                    IdentifierLiteral("AnotherIdentifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral))
                ).hashCode(),
                PrototypeLiteral(
                    IdentifierLiteral("Identifier"),
                    listOf(
                        IdentifierLiteral("ParameterIdentifier"),
                        IdentifierLiteral("AnotherParameterIdentifier")
                    ),
                    Block(ExpressionStatement(FalseLiteral))
                ).hashCode()
            )
        }
    }

    @Nested
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
                assertThat(result.identifier, equalTo(IdentifierLiteral("empty")))
                assertThat(result.parameters, hasSize(0))
                assertThat(result.block, hasSize(0))

                tokens.consumed()
            }
        }

        @Test
        fun `Parse lambda function `() {
            runLiteralTest(
                PrototypeLiteral.Parse,
                """
                function() end
                """.trimIndent()
            ) { result, tokens ->
                assertThat(result.identifier, equalTo(IdentifierLiteral("lambda")))
                assertThat(result.parameters, hasSize(0))
                assertThat(result.block, hasSize(0))

                tokens.consumed()
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
                assertThat(result.identifier, equalTo(IdentifierLiteral("empty_with_single_param")))
                assertThat(result.parameters, equalTo(listOf(IdentifierLiteral("param_one"))))
                assertThat(result.block, hasSize(0))

                tokens.consumed()
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
                assertThat(result.identifier, equalTo(IdentifierLiteral("empty_with_params")))
                assertThat(
                    result.parameters, equalTo(
                        listOf(
                            IdentifierLiteral("one"),
                            IdentifierLiteral("two"),
                            IdentifierLiteral("three")
                        )
                    )
                )
                assertThat(result.block, hasSize(0))

                tokens.consumed()
            }
        }
    }
}