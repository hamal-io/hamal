package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.AbstractAstTest
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.tokenize

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