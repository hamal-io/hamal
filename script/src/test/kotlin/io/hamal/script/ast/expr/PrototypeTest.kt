package io.hamal.script.ast.expr

import io.hamal.script.ast.stmt.Block
import io.hamal.script.ast.stmt.ExpressionStatement
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
        fun `Equals if identifier and parameters are equal`() {
            assertEquals(
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral()))
                ),
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral()))
                )
            )
        }

        @Test
        fun `Not equals if identifier is not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral()))
                ),
                PrototypeLiteral(
                    Identifier("AnotherIdentifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral()))
                )
            )
        }

        @Test
        fun `Not equals if parameters are not equal`() {
            assertNotEquals(
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral()))
                ),
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier"), Identifier("AnotherParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral()))
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
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral()))
                ).hashCode(),
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral()))
                ).hashCode(),
                PrototypeLiteral(
                    Identifier("AnotherIdentifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(TrueLiteral()))
                ).hashCode(),
                PrototypeLiteral(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier"), Identifier("AnotherParameterIdentifier")),
                    Block(ExpressionStatement(FalseLiteral()))
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
                assertThat(result.identifier, equalTo(Identifier("empty")))
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
                assertThat(result.identifier, equalTo(Identifier("empty_with_single_param")))
                assertThat(result.parameters, equalTo(listOf(Identifier("param_one"))))
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