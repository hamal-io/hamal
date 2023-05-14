package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.Expression
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseExpression


data class PrefixExpression(
    val operator: Operator,
    val expression: Expression
) : Expression {
    internal object Parse : ParseExpression<PrefixExpression> {
        override fun invoke(ctx: Context): PrefixExpression {
            return PrefixExpression(
                Operator.Parse(ctx),
                ctx.parseExpression(Precedence.Prefix)
            )
        }
    }

    override fun toString() = "$operator$expression"
}


