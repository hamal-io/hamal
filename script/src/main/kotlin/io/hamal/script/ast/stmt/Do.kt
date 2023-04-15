package io.hamal.script.ast.stmt

import io.hamal.script.ast.Parser
import io.hamal.script.ast.Statement
import io.hamal.script.ast.parseBlockStatement
import io.hamal.script.token.Token

data class Do(
    val blockStatement: Block
) : Statement {

    internal object ParseDo : ParseStatement<Do> {
        override fun invoke(ctx: Parser.Context): Do {
            assert(ctx.isNotEmpty())
            ctx.expectCurrentTokenTypToBe(Token.Type.Do)
            ctx.advance()

            if (ctx.currentTokenType() == Token.Type.End) {
                ctx.advance()
                return Do(Block.empty)
            }
            return Do(ctx.parseBlockStatement())
        }
    }
}