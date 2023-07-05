package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.anotherPosition
import io.hamal.lib.script.impl.ast.stmt.Block
import io.hamal.lib.script.impl.ast.stmt.ExpressionStatement
import io.hamal.lib.script.impl.somePosition
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
        fun `Equal if ident and parameters are equal`() {
            assertEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
                ),
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, FalseLiteral(somePosition)))
                )
            )

            assertEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
                ),
                PrototypeLiteral(
                    anotherPosition,
                    IdentifierLiteral(anotherPosition, "ident"),
                    listOf(IdentifierLiteral(anotherPosition, "ParameterIdentifier")),
                    Block(anotherPosition, ExpressionStatement(anotherPosition, FalseLiteral(anotherPosition)))
                )
            )

        }

        @Test
        fun `Not Equal if identifiers are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
                ),
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "AnotherIdentifier"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, FalseLiteral(somePosition)))
                )
            )
        }

        @Test
        fun `Not Equal if parameters are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
                ),
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(
                        IdentifierLiteral(somePosition, "ParameterIdentifier"),
                        IdentifierLiteral(somePosition, "AnotherParameterIdentifier")
                    ),
                    Block(somePosition, ExpressionStatement(somePosition, FalseLiteral(somePosition)))
                )
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if ident and parameters hashcode are the same`() {
            assertEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(
                        somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition))
                    )
                ).hashCode(),
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, FalseLiteral(somePosition)))
                ).hashCode()
            )

            assertEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(
                        somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition))
                    )
                ).hashCode(),
                PrototypeLiteral(
                    anotherPosition,
                    IdentifierLiteral(anotherPosition, "ident"),
                    listOf(IdentifierLiteral(anotherPosition, "ParameterIdentifier")),
                    Block(anotherPosition, ExpressionStatement(anotherPosition, FalseLiteral(anotherPosition)))
                ).hashCode()
            )

        }

        @Test
        fun `Different hashcode if ident has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
                ).hashCode(),
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "AnotherIdentifier"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, FalseLiteral(somePosition)))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                    Block(somePosition, ExpressionStatement(somePosition, TrueLiteral(somePosition)))
                ).hashCode(),
                PrototypeLiteral(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(
                        IdentifierLiteral(somePosition, "ParameterIdentifier"),
                        IdentifierLiteral(somePosition, "AnotherParameterIdentifier")
                    ),
                    Block(somePosition, ExpressionStatement(somePosition, FalseLiteral(somePosition)))
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
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "empty")))
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
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "lambda")))
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
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "empty_with_single_param")))
                assertThat(result.parameters, equalTo(listOf(IdentifierLiteral(somePosition, "param_one"))))
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
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "empty_with_params")))
                assertThat(
                    result.parameters, equalTo(
                        listOf(
                            IdentifierLiteral(somePosition, "one"),
                            IdentifierLiteral(somePosition, "two"),
                            IdentifierLiteral(somePosition, "three")
                        )
                    )
                )
                assertThat(result.block, hasSize(0))

                tokens.consumed()
            }
        }
    }
}