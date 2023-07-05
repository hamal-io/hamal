package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.PrototypeLiteral

class Prototype(
    override val position: Position,
    val expression: PrototypeLiteral
) : Statement {
    internal object Parse : ParseStatement<Prototype> {
        override fun invoke(ctx: Context): Prototype {
            return Prototype(ctx.currentPosition(), PrototypeLiteral.Parse(ctx))
        }
    }
}