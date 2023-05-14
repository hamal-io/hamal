package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.FalseLiteral
import io.hamal.lib.script.impl.ast.expr.IdentifierExpression
import io.hamal.lib.script.impl.ast.expr.NumberLiteral
import io.hamal.lib.script.impl.ast.expr.TrueLiteral
import io.hamal.lib.script.impl.ast.stmt.Assignment.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class AssignmentTest : AbstractStatementTest() {
    @Nested
    @DisplayName("Global")
    inner class GlobalTest {
        @Nested
        @DisplayName("Parse()")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class ParseTest {
            private fun testCases(): List<Arguments> {
                return listOf(
                    Arguments.of(
                        "some_number=42",
                        Global(IdentifierExpression("some_number"), NumberLiteral(42))
                    ),
                    Arguments.of(
                        "some_var, another_var = true, false",
                        Global(
                            listOf(IdentifierExpression("some_var"), IdentifierExpression("another_var")),
                            listOf(TrueLiteral(), FalseLiteral())
                        )
                    )
                );
            }

            @ParameterizedTest(name = "#{index} - {0}")
            @MethodSource("testCases")
            fun `Global assignment`(code: String, assignment: Assignment) {
                runTest(Global.Parse, code) { result, tokens ->
                    assertThat(result, equalTo(assignment))
                    tokens.wereConsumed()
                }
            }

        }
    }

    @Nested
    @DisplayName("Local")
    inner class LocalTest {
        @Nested
        @DisplayName("Parse()")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class ParseTest {
            private fun testCases(): List<Arguments> {
                return listOf(
                    Arguments.of(
                        "local some_number=42",
                        Local(IdentifierExpression("some_number"), NumberLiteral(42))
                    ),
                    Arguments.of(
                        "local some_var, another_var = true, false",
                        Local(
                            listOf(IdentifierExpression("some_var"), IdentifierExpression("another_var")),
                            listOf(TrueLiteral(), FalseLiteral())
                        )
                    )
                );
            }

            @ParameterizedTest(name = "#{index} - {0}")
            @MethodSource("testCases")
            fun `Local assignment`(code: String, assignment: Assignment) {
                runTest(Local.Parse, code) { result, tokens ->
                    assertThat(result, equalTo(assignment))
                    tokens.wereConsumed()
                }
            }

        }
    }
}