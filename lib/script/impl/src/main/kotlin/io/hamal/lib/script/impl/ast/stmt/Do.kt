package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.token.Token

data class Do(
    val block: BlockStatement
) : Statement {

    internal object Parse : ParseStatement<Do> {
        override fun invoke(ctx: Context): Do {
            require(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Do)
            ctx.advance()

            if (ctx.currentTokenType() == Token.Type.End) {
                ctx.advance()
                return Do(BlockStatement.empty)
            }

            val block = ctx.parseBlockStatement()
            ctx.advance()
            return Do(block)
        }
    }
}