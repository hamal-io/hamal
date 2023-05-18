package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.AbstractAstTest
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.tokenize

internal abstract class AbstractStatementTest : AbstractAstTest() {

    fun <STATEMENT : Statement> runTest(
        parser: ParseStatement<STATEMENT>,
        code: String,
        assertFn: (STATEMENT, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Context(tokens))
        assertFn(result, tokens)
    }

}