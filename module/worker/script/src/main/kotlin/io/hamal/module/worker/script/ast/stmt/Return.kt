package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.expr.Nil
import io.hamal.module.worker.script.ast.parseExpression
import io.hamal.module.worker.script.token.Token

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