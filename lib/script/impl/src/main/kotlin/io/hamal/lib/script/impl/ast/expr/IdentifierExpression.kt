package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type

data class IdentifierExpression(
    override val value: String
) : Identifier, LiteralExpression {
    internal object Parse : ParseLiteralExpression<IdentifierExpression> {
        override fun invoke(ctx: Context): IdentifierExpression {
            assert(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Identifier)
            return IdentifierExpression(token.value)
        }
    }

    override fun toString() = value
}