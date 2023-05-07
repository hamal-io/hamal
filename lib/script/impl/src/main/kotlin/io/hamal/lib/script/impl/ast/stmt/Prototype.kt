package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.impl.ast.Parser.Context
import io.hamal.lib.script.impl.ast.expr.PrototypeLiteral

class Prototype(
    val expression: PrototypeLiteral
) : Statement {
    internal object Parse : ParseStatement<Prototype> {
        override fun invoke(ctx: Context): Prototype {
            return Prototype(PrototypeLiteral.Parse(ctx))
        }
    }
}