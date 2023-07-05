package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Node
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.parseBlockStatement
import io.hamal.lib.script.impl.token.Token
import io.hamal.lib.script.impl.token.Token.Type.End

class DoStmt(
    override val position: Node.Position,
    val block: Block
) : Statement {

    internal object Parse : ParseStatement<DoStmt> {
        override fun invoke(ctx: Context): DoStmt {
            require(ctx.isNotEmpty())
            val position = ctx.currentPosition()
            ctx.expectCurrentTokenTypToBe(Token.Type.Do)
            ctx.advance()
            if (ctx.currentTokenType() == End) {
                ctx.advance()
                return DoStmt(ctx.currentPosition(), Block.empty(ctx.currentPosition()))
            }

            val block = ctx.parseBlockStatement()
            ctx.expectCurrentTokenTypToBe(End)
            ctx.advance()
            return DoStmt(position, block)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DoStmt
        return block == other.block
    }

    override fun hashCode(): Int {
        return block.hashCode()
    }

}