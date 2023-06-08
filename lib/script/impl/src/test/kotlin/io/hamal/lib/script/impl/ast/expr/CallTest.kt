package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.PrecedenceString
import io.hamal.lib.script.impl.ast.Parser
import io.hamal.lib.script.impl.ast.stmt.Call
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
        fun `Equal if identifier and parameters are equal`() {
            assertEquals(
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ),
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                )
            )
        }

        @Test
        fun `Not Equal if identifiers are not equal`() {
            assertNotEquals(
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ),
                CallExpression(
                    IdentifierLiteral("AnotherIdentifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                )
            )
        }

        @Test
        fun `Not Equal if parameters are not equal`() {
            assertNotEquals(
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ),
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(
                        IdentifierLiteral("ParameterIdentifier"),
                        IdentifierLiteral("AnotherParameterIdentifier")
                    )
                )
            )
        }

    }

    @Nested
    inner class HashCodeTest {
        @Test
        fun `Same hashcode if identifier and parameters hashcode are the same`() {
            assertEquals(
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier")),
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    IdentifierLiteral("AnotherIdentifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(IdentifierLiteral("ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    IdentifierLiteral("Identifier"),
                    listOf(
                        IdentifierLiteral("ParameterIdentifier"),
                        IdentifierLiteral("AnotherParameterIdentifier")
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
                IdentifierLiteral("some_fn"),
                """
                ()
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.identifier, equalTo(IdentifierLiteral("some_fn")))
                assertThat(result.parameters, empty())

                tokens.consumed()
            }
        }

        @Test
        fun `Parse single argument`() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierLiteral("some_fn"),
                """
                ('parameter_one')
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.identifier, equalTo(IdentifierLiteral("some_fn")))
                assertThat(result.parameters, equalTo(listOf(StringLiteral("parameter_one"))))

                tokens.consumed()
            }
        }

        @Test
        fun `Parse multiple arguments`() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierLiteral("some_fn"),
                """
                ('parameter_one',1337)
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.identifier, equalTo(IdentifierLiteral("some_fn")))
                assertThat(
                    result.parameters, equalTo(
                        listOf(
                            StringLiteral("parameter_one"),
                            NumberLiteral(1337)
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
                assertThat(result, equalTo(expected));
            }
        }

    }
}