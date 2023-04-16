package io.hamal.script.ast.stmt

import io.hamal.script.ast.Parser
import io.hamal.script.ast.expr.CallExpression
import io.hamal.script.ast.expr.Identifier

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