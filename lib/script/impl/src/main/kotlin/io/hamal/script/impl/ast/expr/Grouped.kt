package io.hamal.script.impl.ast.expr

import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.parseExpression
import io.hamal.script.impl.token.Token

data class GroupedExpression(val expression: Expression) : Expression {
    internal object Parse : ParseExpression<GroupedExpression> {
        override fun invoke(ctx: Parser.Context): GroupedExpression {
            ctx.expectCurrentTokenTypToBe(Token.Type.LeftParenthesis)
            ctx.advance()

            if (ctx.currentTokenType() != Token.Type.RightParenthesis) {
                val result = GroupedExpression(ctx.parseExpression(Precedence.Lowest))
                ctx.advance()
                return result
            }
            return GroupedExpression(NilLiteral())
        }
    }

    override fun toString() = "($expression)"
}