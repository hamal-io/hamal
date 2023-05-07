package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type

data class Identifier(val value: String) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<Identifier> {
        override fun invoke(ctx: Context): Identifier {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Identifier)
            return Identifier(token.value)
        }
    }

    override fun toString() = value
}