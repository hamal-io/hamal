package io.hamal.lib.script.impl.ast.stmt

import io.hamal.lib.script.api.ast.Expression
import io.hamal.lib.script.api.ast.Node.Position
import io.hamal.lib.script.api.ast.Statement
import io.hamal.lib.script.impl.ast.Parser.Context

internal interface ParseStatement<out STATEMENT : Statement> {
    operator fun invoke(ctx: Context): STATEMENT
}


class ExpressionStatement(
    override val position: Position,
    val expression: Expression
) : Statement {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ExpressionStatement
        return expression == other.expression
    }

    override fun hashCode(): Int {
        return expression.hashCode()
    }
}


class Block(
    override val position: Position,
    val statements: List<Statement>,
    override val size: Int
) : Statement, Collection<Statement> {
    constructor(position: Position, vararg statements: Statement) : this(position, statements.toList())
    constructor(position: Position, statements: List<Statement>) : this(position, statements, statements.size)

    companion object {
        fun empty(position: Position) = Block(position, listOf())
    }

    override fun contains(element: Statement) = statements.contains(element)

    override fun containsAll(elements: Collection<Statement>) = statements.containsAll(elements)

    override fun isEmpty() = statements.isEmpty()

    override fun iterator(): Iterator<Statement> = statements.iterator()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Block
        if (statements != other.statements) return false
        return size == other.size
    }

    override fun hashCode(): Int {
        var result = statements.hashCode()
        result = 31 * result + size
        return result
    }
}