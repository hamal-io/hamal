package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.FalseLiteral
import io.hamal.lib.script.impl.ast.expr.IdentifierExpression
import io.hamal.lib.script.impl.ast.expr.NumberLiteral
import io.hamal.lib.script.impl.ast.expr.TrueLiteral
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
                Global(IdentifierExpression("some_number"), NumberLiteral(42))
            ),
            Pair(
                "some_var, another_var = true, false",
                Global(
                    listOf(IdentifierExpression("some_var"), IdentifierExpression("another_var")),
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
                Local(IdentifierExpression("some_number"), NumberLiteral(42))
            ),
            Pair(
                "local some_var, another_var = true, false",
                Local(
                    listOf(IdentifierExpression("some_var"), IdentifierExpression("another_var")),
                    listOf(TrueLiteral(), FalseLiteral())
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