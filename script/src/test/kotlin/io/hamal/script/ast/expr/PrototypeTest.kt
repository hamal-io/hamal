package io.hamal.script.ast.expr

import io.hamal.script.ast.ExpressionStatement
import io.hamal.script.ast.stmt.Block
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
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ),
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                )
            )
        }

        @Test
        fun `Not equals if identifier is not equal`() {
            assertNotEquals(
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ),
                Prototype(
                    Identifier("AnotherIdentifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                )
            )
        }

        @Test
        fun `Not equals if parameters are not equal`() {
            assertNotEquals(
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ),
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier"), Identifier("AnotherParameterIdentifier")),
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
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ).hashCode(),
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if identifier has different hashcode`() {
            assertNotEquals(
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ).hashCode(),
                Prototype(
                    Identifier("AnotherIdentifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(False()))
                ).hashCode()
            )
        }

        @Test
        fun `Different hashcode if parameter has different hashcode`() {
            assertNotEquals(
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier")),
                    Block(ExpressionStatement(True()))
                ).hashCode(),
                Prototype(
                    Identifier("Identifier"),
                    listOf(Identifier("ParameterIdentifier"), Identifier("AnotherParameterIdentifier")),
                    Block(ExpressionStatement(False()))
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
                Prototype.Parse,
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
                Prototype.Parse,
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
                Prototype.Parse,
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