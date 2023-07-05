package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token.Type.LeftParenthesis
import io.hamal.lib.script.impl.token.Token.Type.RightParenthesis

class GroupedExpression(
    override val position: Node.Position,
    val expression: Expression
) : Expression {
    internal object Parse : ParseExpression<GroupedExpression> {
        override fun invoke(ctx: Context): GroupedExpression {
            val position = ctx.currentPosition()
            ctx.expectCurrentTokenTypToBe(LeftParenthesis)
            ctx.advance()

            if (ctx.currentTokenType() == RightParenthesis) {
                ctx.advance()
                return GroupedExpression(position, NilLiteral(ctx.currentPosition()))
            }

            val result = GroupedExpression(position, ctx.parseExpression(Precedence.Lowest))
            ctx.expectCurrentTokenTypToBe(RightParenthesis)
            ctx.advance()
            return result
        }
    }

    override fun toString() = "($expression)"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GroupedExpression
        return expression == other.expression
    }

    override fun hashCode(): Int {
        return expression.hashCode()
    }

}