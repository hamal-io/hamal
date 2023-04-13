package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.expr.NilLiteral
import io.hamal.module.worker.script.ast.parseExpression
import io.hamal.module.worker.script.token.Token.Type.End
import io.hamal.module.worker.script.token.Token.Type.Return

class ReturnStatement(
    val returnValue: Expression
) : Statement {
    internal object ParseReturnStatement : ParseStatement {
        override fun invoke(ctx: Parser.Context): ReturnStatement {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Return)
            ctx.advance()

            //return token must be followed by end
            //see: https://www.lua.org/pil/4.4.html
            var result = ReturnStatement(NilLiteral())
            if (ctx.currentTokenType() != End) {
                result = ReturnStatement(ctx.parseExpression())
                ctx.advance()
            }
            ctx.expectCurrentTokenTypToBe(End)
            return result
        }
    }
}