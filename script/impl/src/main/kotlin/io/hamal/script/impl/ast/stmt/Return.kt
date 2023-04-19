package io.hamal.script.impl.ast.stmt

import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.expr.Expression
import io.hamal.script.impl.ast.expr.NilLiteral
import io.hamal.script.impl.ast.parseExpression
import io.hamal.script.impl.token.Token

data class Return(
    val expression: Expression
) : Statement {
    internal object Parse : ParseStatement<Return> {
        override fun invoke(ctx: Parser.Context): Return {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Return)
            ctx.advance()

            //return token must be followed by end
            //see: https://www.lua.org/pil/4.4.html
            var result = Return(NilLiteral())
            if (ctx.currentTokenType() != Token.Type.End) {
                result = Return(ctx.parseExpression())
                ctx.advance()
            }
            ctx.expectCurrentTokenTypToBe(Token.Type.End)
            return result
        }
    }
}