package io.hamal.script.ast.stmt

import io.hamal.script.ast.expr.False
import io.hamal.script.ast.expr.Identifier
import io.hamal.script.ast.expr.Number
import io.hamal.script.ast.expr.True
import io.hamal.script.ast.stmt.Assignment.*
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
                        Global(Identifier("some_number"), Number(42))
                    ),
                    Arguments.of(
                        "some_var, another_var = true, false",
                        Global(
                            listOf(Identifier("some_var"), Identifier("another_var")),
                            listOf(True(), False())
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
                        Local(Identifier("some_number"), Number(42))
                    ),
                    Arguments.of(
                        "local some_var, another_var = true, false",
                        Local(
                            listOf(Identifier("some_var"), Identifier("another_var")),
                            listOf(True(), False())
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