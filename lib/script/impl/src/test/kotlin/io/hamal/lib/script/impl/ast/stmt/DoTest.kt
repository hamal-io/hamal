package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.NilLiteral
import io.hamal.lib.script.impl.ast.stmt.Do.Parse
import io.hamal.lib.script.impl.token.Token.Type.End
import io.hamal.lib.script.impl.token.Token.Type.Eof
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class DoTest : AbstractStatementTest() {
    @TestFactory
    fun parse() = listOf(
        Pair(
            "do end",
            Do(BlockStatement.empty)
        ),
        Pair(
            "do return end",
            Do(BlockStatement(Return(NilLiteral)))
        )
    ).map { (code, expected) ->
        DynamicTest.dynamicTest(code) {
            runTest(Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.inOrder(End, Eof)
            }
        }
    }
}