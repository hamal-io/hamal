package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type.End

data class DoStmt(
    val block: Block
) : Statement {

    internal object Parse : ParseStatement<DoStmt> {
        override fun invoke(ctx: Context): DoStmt {
            require(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Do)
            ctx.advance()
            if (ctx.currentTokenType() == End) {
                ctx.advance()
                return DoStmt(Block.empty)
            }

            val block = ctx.parseBlockStatement()
            ctx.expectCurrentTokenTypToBe(End)
            ctx.advance()
            return DoStmt(block)
        }
    }
}