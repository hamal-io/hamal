package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.NilLiteral
import io.hamal.lib.script.impl.ast.stmt.DoStmt.Parse
import io.hamal.lib.script.impl.somePosition
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class DoStmtTest : AbstractStatementTest() {
    @TestFactory
    fun parse() = listOf(
        "do end" to DoStmt(somePosition, Block.empty(somePosition)),
        "do return end" to DoStmt(somePosition, Block(somePosition, Return(somePosition, NilLiteral(somePosition))))
    ).map { (code, expected) ->
        dynamicTest(code) {
            runTest(Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.consumed()
            }
        }
    }
}