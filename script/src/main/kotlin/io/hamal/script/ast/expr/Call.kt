package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type

class CallExpression(
    val identifier: Identifier,
    val parameters: List<Expression>
) : Expression {
    internal object Parse : ParseInfixExpression {
        override fun invoke(ctx: Parser.Context, lhs: Expression): CallExpression {
            return CallExpression(lhs as Identifier, ctx.parseArguments())
        }

        private fun Parser.Context.parseArguments(): List<Expression> {
            expectCurrentTokenTypToBe(Type.LeftParenthesis)
            advance()

            if (currentTokenType() == Type.RightParenthesis) {
                advance()
                return listOf()
            }
            TODO()
        }
    }
}