package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.Assignment.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.dynamicTest

internal class AssignmentTest : AbstractStatementTest() {
    @Nested
    @DisplayName("Global")
    inner class GlobalTest {
        @TestFactory
        fun parse() = listOf(
            Pair(
                "some_number=42",
                Global(IdentifierLiteral("some_number"), NumberLiteral(42))
            ),
            Pair(
                "some_var, another_var = true, false",
                Global(
                    listOf(IdentifierLiteral("some_var"), IdentifierLiteral("another_var")),
                    listOf(TrueLiteral(), FalseLiteral())
                )
            )
        ).map { (code, expected) ->
            dynamicTest(code) {
                runTest(Global.Parse, code) { result, tokens ->
                    assertThat(result, equalTo(expected))
                    tokens.wereConsumed()
                }
            }
        }
    }

    @Nested
    @DisplayName("Local")
    inner class LocalTest {

        @TestFactory
        fun parse() = listOf(
            Pair(
                "local some_number=42",
                Local(IdentifierLiteral("some_number"), NumberLiteral(42))
            ),
            Pair(
                "local some_var, another_var = true, false",
                Local(
                    listOf(IdentifierLiteral("some_var"), IdentifierLiteral("another_var")),
                    listOf(TrueLiteral(), FalseLiteral())
                )
            ),
            Pair(
                "local x = some_fn('y')",
                Local(
                    listOf(IdentifierLiteral("x")),
                    listOf(CallExpression(IdentifierLiteral("some_fn"), listOf(StringLiteral("y"))))
                )
            )

        ).map { (code, expected) ->
            dynamicTest(code) {
                runTest(Local.Parse, code) { result, tokens ->
                    assertThat(result, equalTo(expected))
                    tokens.wereConsumed()
                }
            }
        }
    }
}