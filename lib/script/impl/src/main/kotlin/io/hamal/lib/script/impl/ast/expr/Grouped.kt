package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token

data class GroupedExpression(val expression: Expression) : Expression {
    internal object Parse : ParseExpression<GroupedExpression> {
        override fun invoke(ctx: Context): GroupedExpression {
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