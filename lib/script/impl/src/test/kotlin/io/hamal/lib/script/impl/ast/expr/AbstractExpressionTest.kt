package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.AbstractAstTest
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.tokenize

internal abstract class AbstractExpressionTest : AbstractAstTest() {

    fun <EXPRESSION : LiteralExpression> runLiteralTest(
        parser: ParseLiteralExpression<EXPRESSION>,
        code: String,
        assertFn: (EXPRESSION, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Context(tokens))
        assertFn(result, tokens)
    }

    fun <EXPRESSION : Expression> runTest(
        parser: ParseExpression<EXPRESSION>,
        code: String,
        assertFn: (EXPRESSION, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Context(tokens))
        assertFn(result, tokens)
    }

    fun runInfixTest(
        parser: ParseInfixExpression,
        lhs: Expression,
        code: String,
        assertFn: (Expression, ArrayDeque<Token>) -> Unit
    ) {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser.invoke(Context(tokens), lhs)
        assertFn(result, tokens)
    }

}