package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.ParsePrefixExpression
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.tokenize
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal abstract class AbstractAstTest {

    fun parseExpression(parser: ParsePrefixExpression, code: String): Expression {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser(Parser.Context(tokens))
        assertThat("No tokens were consumed except EOF", tokens.size, equalTo(2))
        assertThat("Last token must be EOF", tokens[1].type, equalTo(Token.Type.Eof))
        return result
    }

    fun Expression.verifyPrecedence(expected: String) {
        assertThat(PrecedenceString.of(this), equalTo(expected))
    }
}