package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.impl.ast.Parser

class RequireExpression(
    val module: Expression
) : Expression {

    internal object Parse : ParseExpression<RequireExpression> {
        override fun invoke(ctx: Parser.Context): RequireExpression {
            TODO("Not yet implemented")
        }
    }

}