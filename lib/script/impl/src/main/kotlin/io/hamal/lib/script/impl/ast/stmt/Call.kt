package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.CallExpression
import io.hamal.lib.script.impl.ast.expr.IdentifierLiteral

class Call(
    val expression: CallExpression
) : Statement {
    internal object Parse : ParseStatement<Call> {
        override fun invoke(ctx: Context): Call {
            val ident = IdentifierLiteral.Parse(ctx)
            return Call(CallExpression.Parse(ctx, ident))
        }
    }
}