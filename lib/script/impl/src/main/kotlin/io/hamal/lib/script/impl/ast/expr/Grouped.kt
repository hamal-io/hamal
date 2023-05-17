package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token.Type.LeftParenthesis
import io.hamal.lib.script.impl.token.Token.Type.RightParenthesis

data class GroupedExpression(val expression: Expression) : Expression {
    internal object Parse : ParseExpression<GroupedExpression> {
        override fun invoke(ctx: Context): GroupedExpression {
            ctx.expectCurrentTokenTypToBe(LeftParenthesis)
            ctx.advance()

            if (ctx.currentTokenType() == RightParenthesis) {
                ctx.advance()
                return GroupedExpression(NilLiteral)
            }

            val result = GroupedExpression(ctx.parseExpression(Precedence.Lowest))
            ctx.expectCurrentTokenTypToBe(RightParenthesis)
            ctx.advance()
            return result
        }
    }

    override fun toString() = "($expression)"
}