package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.NilLiteral
import io.hamal.lib.script.impl.ast.parseExpression
import io.hamal.lib.script.impl.token.Token

data class Return(
    override val position: Position,
    val expression: Expression
) : Statement {
    internal object Parse : ParseStatement<Return> {
        override fun invoke(ctx: Context): Return {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            ctx.expectCurrentTokenTypToBe(Token.Type.Return)
            ctx.advance()

            //return token must be followed by end
            //see: https://www.lua.org/pil/4.4.html
            var result = Return(position, NilLiteral(ctx.currentPosition()))
            if (ctx.currentTokenType() != Token.Type.End) {
                result = Return(ctx.currentPosition(), ctx.parseExpression())
            }
            ctx.expectCurrentTokenTypToBe(Token.Type.End)
            return result
        }
    }
}