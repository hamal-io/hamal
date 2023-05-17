package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.expr.NilLiteral
import io.hamal.lib.script.impl.ast.expr.NumberLiteral
import io.hamal.lib.script.impl.token.Token.Type.End
import io.hamal.lib.script.impl.token.Token.Type.Eof
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class ReturnTest : AbstractStatementTest() {
    @TestFactory
    fun parse() = listOf(
        Pair(
            "return end",
            Return(NilLiteral())
        ),
        Pair(
            "return 1 end",
            Return(NumberLiteral(1))
        )
    ).map { (code, expected) ->
        DynamicTest.dynamicTest(code) {
            runTest(Return.Parse, code) { result, tokens ->
                assertThat(result, equalTo(expected))
                tokens.inOrder(End, Eof)
            }
        }
    }
}