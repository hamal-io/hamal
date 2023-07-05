package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression


data class PrefixExpression(
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
}


