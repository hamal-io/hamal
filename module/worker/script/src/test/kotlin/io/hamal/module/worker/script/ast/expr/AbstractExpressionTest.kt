package io.hamal.module.worker.script.ast.expr

import io.hamal.module.worker.script.ast.AbstractAstTest
import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.tokenize
import kotlin.String

internal abstract class AbstractExpressionTest : AbstractAstTest() {

    fun <EXPRESSION : LiteralExpression> runLiteralTest(
        parser: ParseLiteralExpression<EXPRESSION>,
        code: String,
        assertFn: (EXPRESSION, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Parser.Context(tokens))
        assertFn(result, tokens)
    }

    fun <EXPRESSION : Expression> runTest(
        parser: ParsePrefixExpression<EXPRESSION>,
        code: String,
        assertFn: (EXPRESSION, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Parser.Context(tokens))
        assertFn(result, tokens)
    }

}