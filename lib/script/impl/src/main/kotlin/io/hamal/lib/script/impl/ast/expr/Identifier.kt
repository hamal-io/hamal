package io.hamal.lib.script.impl.ast.expr

import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.token.Token.Type


data class IdentifierLiteral(
    override val position: Position,
    val value: String
) : LiteralExpression {
    internal object Parse : ParseLiteralExpression<IdentifierLiteral> {
        override fun invoke(ctx: Context): IdentifierLiteral {
            val position = ctx.currentPosition()
            require(ctx.isNotEmpty())
            val token = ctx.currentToken()
            assert(token.type == Type.Ident)
            ctx.advance()
            return IdentifierLiteral(position, token.value)
        }
    }

    override fun toString() = value
}

internal fun Context.parseIdentifier(): IdentifierLiteral {
    return IdentifierLiteral.Parse(this)
}