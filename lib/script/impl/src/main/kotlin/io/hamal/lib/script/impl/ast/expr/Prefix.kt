package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression


class PrefixExpression(
    override val position: Position,
    val operator: Operator,
    val expression: Expression
) : Expression {
    internal object Parse : ParseExpression<PrefixExpression> {
        override fun invoke(ctx: Context): PrefixExpression {
            val position = ctx.currentPosition()
            return PrefixExpression(
                position,
                Operator.Parse(ctx),
                ctx.parseExpression(Precedence.Prefix)
            )
        }
    }

    override fun toString() = "$operator$expression"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PrefixExpression
        if (operator != other.operator) return false
        return expression == other.expression
    }

    override fun hashCode(): Int {
        var result = operator.hashCode()
        result = 31 * result + expression.hashCode()
        return result
    }

}


