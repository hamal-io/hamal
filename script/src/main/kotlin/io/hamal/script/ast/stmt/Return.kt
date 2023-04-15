package io.hamal.script.ast.stmt

import io.hamal.script.ast.Expression
import io.hamal.script.ast.Parser
import io.hamal.script.ast.Statement
import io.hamal.script.ast.expr.Nil
import io.hamal.script.ast.parseExpression
import io.hamal.script.token.Token

data class Return(
    val returnValue: Expression
) : Statement {
    internal object ParseReturn : ParseStatement<Return> {
        override fun invoke(ctx: Parser.Context): Return {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Return)
            ctx.advance()

            //return token must be followed by end
            //see: https://www.lua.org/pil/4.4.html
            var result = Return(Nil())
            if (ctx.currentTokenType() != Token.Type.End) {
                result = Return(ctx.parseExpression())
                ctx.advance()
            }
            ctx.expectCurrentTokenTypToBe(Token.Type.End)
            return result
        }
    }
}