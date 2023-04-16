package io.hamal.script.ast.stmt

import io.hamal.script.ast.Parser
import io.hamal.script.ast.expr.Expression
import io.hamal.script.ast.expr.Identifier
import io.hamal.script.ast.expr.Precedence
import io.hamal.script.ast.parseExpression
import io.hamal.script.token.Token

class Call(
    val identifier: Identifier,
    val parameters: List<Expression>
) : Statement {
    internal object Parse : ParseStatement<Call> {
        override fun invoke(ctx: Parser.Context): Call {
            val identifier = Identifier.Parse(ctx)
            ctx.advance()
            return Call(identifier, ctx.parseParameters())
        }

        private fun Parser.Context.parseParameters(): List<Expression> {
            expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            advance()

            if (currentTokenType() == Token.Type.RightParenthesis) {
                advance()
                return listOf()
            }
//            ctx
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
}