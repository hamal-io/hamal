package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.parseBlockStatement
import io.hamal.module.worker.script.token.Token.Type.Do
import io.hamal.module.worker.script.token.Token.Type.End

class DoStatement(
    val blockStatement: BlockStatement
) : Statement {

    internal object ParseDoBlock : ParseStatement {
        override fun invoke(ctx: Parser.Context): DoStatement {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Do)
            ctx.advance()

            if (ctx.currentTokenType() == End) {
                ctx.advance()
                return DoStatement(BlockStatement.empty)
            }

            val statement = ctx.parseBlockStatement()
            ctx.advance()
            return DoStatement(statement)
        }

    }
}