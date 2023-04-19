package io.hamal.script.impl.ast.expr

import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.parseExpression
import io.hamal.script.impl.token.Token

class CallExpression(
    val identifier: Identifier,
    val parameters: List<Expression>
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): CallExpression {
            return CallExpression(
                lhs as Identifier,
                ctx.parseParameters()
            )
        }

        private fun Parser.Context.parseParameters(): List<Expression> {
            expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            advance()

            if (currentTokenType() == Token.Type.RightParenthesis) {
                advance()
                return listOf()
            }

            val result = mutableListOf<Expression>()
            while (currentTokenType() != Token.Type.RightParenthesis) {
                result.add(parseExpression(Precedence.Lowest))
                advance()
                if (currentTokenType() == Token.Type.Comma) {
                    advance()
                }
            }
            advance()
            return result
        }
    }

    override fun toString() = "$identifier(${parameters.joinToString(",") { it.toString() }})"
}