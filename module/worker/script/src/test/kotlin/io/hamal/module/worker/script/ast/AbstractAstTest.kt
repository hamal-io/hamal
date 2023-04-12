package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.tokenize
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal abstract class AbstractAstTest {

    fun <EXPRESSION : Expression> parseExpression(parser: ParsePrefixExpression<EXPRESSION>, code: String): EXPRESSION {
        val tokens = ArrayDeque(tokenize(code))
        val result = parser(tokens)
        assertThat("All tokens were consumed except EOF", tokens.size, equalTo(1))
        assertThat("Last token must be EOF", tokens.first().type, equalTo(Token.Type.Eof))
        return result
    }

    fun <EXPRESSION : Expression> EXPRESSION.verifyPrecedence(expected: String) {
        val v = PrecedenceTestVisitor()
        accept(v)
        assertThat(v.toString(), equalTo(expected))
    }
}