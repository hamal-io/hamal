package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Identifier
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type

data class IdentifierLiteral(
    override val value: String
) : Identifier, LiteralExpression {
    internal object Parse : ParseLiteralExpression<IdentifierLiteral> {
        override fun invoke(ctx: Context): IdentifierLiteral {
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Identifier)
            return IdentifierLiteral(token.value)
        }
    }

    override fun toString() = value
}