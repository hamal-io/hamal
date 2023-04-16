package io.hamal.script.ast.stmt

import io.hamal.script.ast.Parser
import io.hamal.script.ast.expr.PrototypeLiteral

class Prototype(
    val expression: PrototypeLiteral
) : Statement{
    internal object Parse : ParseStatement<Prototype>{
        override fun invoke(ctx: Parser.Context): Prototype {
            return Prototype(PrototypeLiteral.Parse(ctx))
        }
    }
}