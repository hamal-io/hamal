package io.hamal.script.ast.expr

import io.hamal.script.ast.Parser
import io.hamal.script.token.Token.Type

data class Identifier(val value: String) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<Identifier> {
        override fun invoke(ctx: Parser.Context): Identifier {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Identifier)
            return Identifier(token.value)
        }
    }

    override fun toString() = value
}