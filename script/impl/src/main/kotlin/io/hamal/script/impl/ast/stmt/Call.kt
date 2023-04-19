package io.hamal.script.impl.ast.stmt

import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.expr.CallExpression
import io.hamal.script.impl.ast.expr.Identifier

class Call(
    val expression: CallExpression
) : Statement {
    internal object Parse : ParseStatement<Call> {
        override fun invoke(ctx: Parser.Context): Call {
            val identifier = Identifier.Parse(ctx)
            ctx.advance()
            return Call(CallExpression.Parse(ctx, identifier))
        }
    }
}