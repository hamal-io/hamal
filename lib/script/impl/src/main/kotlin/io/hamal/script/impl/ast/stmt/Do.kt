package io.hamal.script.impl.ast.stmt

import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.parseBlockStatement
import io.hamal.script.impl.token.Token

data class Do(
    val block: Block
) : Statement {

    internal object Parse : ParseStatement<Do> {
        override fun invoke(ctx: Parser.Context): Do {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Do)
            ctx.advance()

            if (ctx.currentTokenType() == Token.Type.End) {
                ctx.advance()
                return Do(Block.empty)
            }

            val block = ctx.parseBlockStatement()
            ctx.advance()
            return Do(block)
        }
    }
}