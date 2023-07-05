package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.*
import io.hamal.lib.script.impl.ast.stmt.Assignment.Global
import io.hamal.lib.script.impl.ast.stmt.Assignment.Local
import io.hamal.lib.script.impl.somePosition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

internal class AssignmentTest : AbstractStatementTest() {
    @Nested
    inner class GlobalTest {
        @TestFactory
        fun parse() = listOf(
            "some_number=42" to Global(
                somePosition, IdentifierLiteral(somePosition, "some_number"), NumberLiteral(
                    somePosition, 42
                )
            ),
            "some_var, another_var = true, false" to Global(
                somePosition,
                listOf(IdentifierLiteral(somePosition, "some_var"), IdentifierLiteral(somePosition, "another_var")),
                listOf(TrueLiteral(somePosition), FalseLiteral(somePosition))
            )
        ).map { (code, expected) ->
            dynamicTest(code) {
                runTest(Global.Parse, code) { result, tokens ->
                    assertThat(result, equalTo(expected))
                    tokens.consumed()
                }
            }
        }
    }

    @Nested
    inner class LocalTest {

        @TestFactory
        fun parse() = listOf(
            "local some_number=42" to Local(
                somePosition,
                IdentifierLiteral(somePosition, "some_number"),
                NumberLiteral(somePosition, 42)
            ),
            "local some_var, another_var = true, false" to Local(
                somePosition,
                listOf(IdentifierLiteral(somePosition, "some_var"), IdentifierLiteral(somePosition, "another_var")),
                listOf(TrueLiteral(somePosition), FalseLiteral(somePosition))
            ),
            "local x = some_fn('y')" to Local(
                somePosition,
                listOf(IdentifierLiteral(somePosition, "x")),
                listOf(
                    CallExpression(
                        somePosition, IdentifierLiteral(somePosition, "some_fn"), listOf(
                            StringLiteral(
                                somePosition, "y"
                            )
                        )
                    )
                )
            )
        ).map { (code, expected) ->
            dynamicTest(code) {
                runTest(Local.Parse, code) { result, tokens ->
                    assertThat(result, equalTo(expected))
                    tokens.consumed()
                }
            }
        }
    }
}