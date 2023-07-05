package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.PrecedenceString
import io.hamal.lib.script.impl.anotherPosition
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.stmt.Call
import io.hamal.lib.script.impl.somePosition
import io.hamal.lib.script.impl.token.tokenize
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class CallExpressionTest : AbstractExpressionTest() {
    @Nested
    inner class EqualsTest {
        @Test
        fun `Equal if ident and parameters are equal`() {
            assertEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ),
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                )
            )

            assertEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ),
                CallExpression(
                    anotherPosition,
                    IdentifierLiteral(anotherPosition, "ident"),
                    listOf(IdentifierLiteral(anotherPosition, "ParameterIdentifier"))
                )
            )

        }

        @Test
        fun `Not Equal if identifiers are not equal`() {
            assertNotEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ),
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "AnotherIdentifier"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                )
            )

            assertNotEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ),
                CallExpression(
                    anotherPosition,
                    IdentifierLiteral(anotherPosition, "AnotherIdentifier"),
                    listOf(IdentifierLiteral(anotherPosition, "ParameterIdentifier"))
                )
            )

        }

        @Test
        fun `Not Equal if parameters are not equal`() {
            assertNotEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ),
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(
                        IdentifierLiteral(somePosition, "ParameterIdentifier"),
                        IdentifierLiteral(somePosition, "AnotherParameterIdentifier")
                    )
                )
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if ident and parameters hashcode are the same`() {
            assertEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier")),
                ).hashCode()
            )

            assertEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    anotherPosition,
                    IdentifierLiteral(anotherPosition, "ident"),
                    listOf(IdentifierLiteral(anotherPosition, "ParameterIdentifier")),
                ).hashCode()
            )

        }

        @Test
        fun `Different hashcode if ident has different hashcode`() {
            assertNotEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "AnotherIdentifier"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(IdentifierLiteral(somePosition, "ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    somePosition,
                    IdentifierLiteral(somePosition, "ident"),
                    listOf(
                        IdentifierLiteral(somePosition, "ParameterIdentifier"),
                        IdentifierLiteral(somePosition, "AnotherParameterIdentifier")
                    )
                ).hashCode()
            )
        }
    }

    @Nested
    inner class ParseTest {
        @Test
        fun `Parse empty `() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierLiteral(somePosition, "some_fn"),
                """
                ()
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "some_fn")))
                assertThat(result.parameters, empty())

                tokens.consumed()
            }
        }

        @Test
        fun `Parse single argument`() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierLiteral(somePosition, "some_fn"),
                """
                ('parameter_one')
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "some_fn")))
                assertThat(result.parameters, equalTo(listOf(StringLiteral(somePosition, "parameter_one"))))

                tokens.consumed()
            }
        }

        @Test
        fun `Parse multiple arguments`() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierLiteral(somePosition, "some_fn"),
                """
                ('parameter_one',1337)
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.ident, equalTo(IdentifierLiteral(somePosition, "some_fn")))
                assertThat(
                    result.parameters, equalTo(
                        listOf(
                            StringLiteral(somePosition, "parameter_one"),
                            NumberLiteral(somePosition, 1337)
                        )
                    )
                )

                tokens.consumed()
            }
        }

        @TestFactory
        fun `Call functions with expressions`() = listOf(
            "fn(1)" to "fn(1)",
            "fn(1,2)" to "fn(1,2)",
            "fn(1 + 2)" to "fn((1 + 2))",
            "fn(1 + 2 - 3)" to "fn(((1 + 2) - 3))",
            "fn(1 + 2 - 3 == 1 + 2 - 3)" to "fn((((1 + 2) - 3) == ((1 + 2) - 3)))",
            "fn(1 ^ 2)" to "fn((1 ^ 2))",
            "fn(1 .. 2)" to "fn((1 .. 2))",
            "fn(1 ^ 2 == 1 ^ 2)" to "fn(((1 ^ 2) == (1 ^ 2)))",
            "fn(1 .. 2 == 1 .. 2)" to "fn(((1 .. 2) == (1 .. 2)))",
            "fn(1 ^ 2 ^ 3)" to "fn((1 ^ (2 ^ 3)))",
            "fn(1 .. 2 .. 3)" to "fn((1 .. (2 .. 3)))",
            "fn(1 ^ 2 ^ 3== 1 ^ 2 ^ 3)" to "fn(((1 ^ (2 ^ 3)) == (1 ^ (2 ^ 3))))",
            "fn(1 .. 2 .. 3== 1 .. 2 .. 3)" to "fn(((1 .. (2 .. 3)) == (1 .. (2 .. 3))))",
            "fn(1 == 3)" to "fn((1 == 3))",
            "fn(1 ~= 3)" to "fn((1 ~= 3))",
        ).map { (code, expected) ->
            dynamicTest(code) {
                val tokens = tokenize(code)

                val blockStatement = Parser.DefaultImpl.parse(Parser.Context(ArrayDeque(tokens)))
                assertThat(blockStatement.statements, hasSize(1))
                val statement = blockStatement.statements.first()
                require(statement is Call)

                val result = PrecedenceString.of(statement.expression)
                assertThat(result, equalTo(expected))
            }
        }

    }
}