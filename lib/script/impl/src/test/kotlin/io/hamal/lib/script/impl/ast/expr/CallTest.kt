package io.hamal.lib.script.impl.ast.expr

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CallTest : AbstractExpressionTest() {

    @Nested
    @DisplayName("equals()")
    inner class EqualsTest {
        @Test
        fun `Equal if identifier and parameters are equal`() {
            assertEquals(
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ),
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                )
            )
        }

        @Test
        fun `Not Equal if identifiers are not equal`() {
            assertNotEquals(
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ),
                CallExpression(
                    IdentifierExpression("AnotherIdentifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                )
            )
        }

        @Test
        fun `Not Equal if parameters are not equal`() {
            assertNotEquals(
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ),
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(
                        IdentifierExpression("ParameterIdentifier"),
                        IdentifierExpression("AnotherParameterIdentifier")
                    )
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
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier")),
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    IdentifierExpression("AnotherIdentifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(IdentifierExpression("ParameterIdentifier"))
                ).hashCode(),
                CallExpression(
                    IdentifierExpression("Identifier"),
                    listOf(
                        IdentifierExpression("ParameterIdentifier"),
                        IdentifierExpression("AnotherParameterIdentifier")
                    )
                ).hashCode()
            )
        }
    }

    @Nested
    @DisplayName("Parse()")
    inner class ParseTest {
        @Test
        fun `Parse empty `() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierExpression("some_fn"),
                """
                ()
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.identifier, equalTo(IdentifierExpression("some_fn")))
                assertThat(result.parameters, empty())

                tokens.wereConsumed()
            }
        }

        @Test
        fun `Parse single argument`() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierExpression("some_fn"),
                """
                ('parameter_one')
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.identifier, equalTo(IdentifierExpression("some_fn")))
                assertThat(result.parameters, equalTo(listOf(StringLiteral("parameter_one"))))

                tokens.wereConsumed()
            }
        }

        @Test
        fun `Parse multiple arguments`() {
            runInfixTest(
                CallExpression.Parse,
                IdentifierExpression("some_fn"),
                """
                ('parameter_one',1337)
                """.trimIndent()
            ) { result, tokens ->
                require(result is CallExpression)
                assertThat(result.identifier, equalTo(IdentifierExpression("some_fn")))
                assertThat(
                    result.parameters, equalTo(
                        listOf(
                            StringLiteral("parameter_one"),
                            NumberLiteral(1337)
                        )
                    )
                )

                tokens.wereConsumed()
            }
        }
    }
}