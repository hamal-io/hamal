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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Prototype
        return expression == other.expression
    }

    override fun hashCode(): Int {
        return expression.hashCode()
    }
}