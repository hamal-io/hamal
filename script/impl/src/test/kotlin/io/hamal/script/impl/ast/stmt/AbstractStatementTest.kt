package io.hamal.script.impl.ast.stmt

import io.hamal.script.impl.ast.AbstractAstTest
import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.token.Token
import io.hamal.script.impl.token.tokenize

internal abstract class AbstractStatementTest : AbstractAstTest() {

    fun <STATEMENT : Statement> runTest(
        parser: ParseStatement<STATEMENT>,
        code: String,
        assertFn: (STATEMENT, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Parser.Context(tokens))
        assertFn(result, tokens)
    }

}