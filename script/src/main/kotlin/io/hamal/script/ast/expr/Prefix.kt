package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.ast.parseExpression


data class PrefixExpression(
    val operator: Operator,
    val expression: Expression
) : Expression {
    internal object Parse : ParseExpression<PrefixExpression> {
        override fun invoke(ctx: Parser.Context): PrefixExpression {
            return PrefixExpression(
                Operator.Parse(ctx),
                ctx.parseExpression(Precedence.Prefix)
            )
        }
    }

    override fun toString() = "$operator$expression"
}


