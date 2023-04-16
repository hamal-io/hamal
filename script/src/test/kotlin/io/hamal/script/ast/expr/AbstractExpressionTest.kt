package io.hamal.script.ast.expr

import io.hamal.script.ast.AbstractAstTest
import io.hamal.script.ast.Parser
import io.hamal.script.token.Token
import io.hamal.script.token.tokenize
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
        parser: ParseExpression<EXPRESSION>,
        code: String,
        assertFn: (EXPRESSION, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Parser.Context(tokens))
        assertFn(result, tokens)
    }
}