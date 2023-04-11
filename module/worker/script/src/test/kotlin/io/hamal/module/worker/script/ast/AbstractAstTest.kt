package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.Tokenizer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

internal abstract class AbstractAstTest {

    fun <NODE : Node> parse(parser: NodeParser<NODE>, code: String): NODE {
        val tokens = allTokens(code)
        val result = parser(tokens)
        assertThat("All tokens were consumed except EOF", tokens.size, equalTo(1))
        assertThat("Last token must be EOF", tokens.first().type, equalTo(Token.Type.EOF))
        return result
    }

    fun <NODE : Node> NODE.verifyPrecedence(expected: String) {
        val v = PrecedenceTestVisitor()
        accept(v)
        assertThat(v.toString(), equalTo(expected))
    }

    private fun allTokens(code: String): ArrayDeque<Token> {
        val tokenizer = Tokenizer.DefaultImpl(code)
        val result = ArrayDeque<Token>()
        while (true) {
            val current = tokenizer.nextToken()
            result.add(current)
            if (current.type == Token.Type.EOF) {
                return result
            }
        }
    }
    
}