package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.literal.LiteralNil
import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.token.TokenLine
import io.hamal.module.worker.script.token.TokenPosition
import io.hamal.module.worker.script.token.Tokenizer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertTrue

internal abstract class AbstractAstTest {

    fun <NODE : Node> parse(parser: Parser<NODE>, code: String): Node = parser(allTokens(code))

    fun <NODE : Node> NODE.verifyPrecedence(expected: String) {
        val v = PrecedenceTestVisitor()
        accept(v)
        assertThat(v.toString(), equalTo(expected))
    }

    private fun allTokens(code: String): List<Token> {
        val tokenizer = Tokenizer.DefaultImpl(code)
        val result = mutableListOf<Token>()
        while (true) {
            val current = tokenizer.nextToken()
            result.add(current)
            if (current.type == Token.Type.EOF) {
                return result
            }
        }
    }

    fun assertNilLiteral(expr: Node) {
        assertTrue(expr is LiteralNil, "Expected expression to be LiteralNil")
    }

    fun assertPosition(node: Node, expectedLine: Int, expectedPosition: Int) {
        assertThat(node.token.line, equalTo(TokenLine(expectedLine)))
        assertThat(node.token.position, equalTo(TokenPosition(expectedPosition)))
    }

}