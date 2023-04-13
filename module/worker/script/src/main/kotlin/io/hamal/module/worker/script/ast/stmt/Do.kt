package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.parseBlockStatement
import io.hamal.module.worker.script.token.Token

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

            val statement = ctx.parseBlockStatement()
            return Do(statement)
        }

    }
}