package io.hamal.script.impl.ast.stmt

import io.hamal.script.impl.ast.Node
import io.hamal.script.impl.ast.Parser
import io.hamal.script.impl.ast.expr.Expression

internal interface ParseStatement<out STATEMENT : Statement> {
    operator fun invoke(ctx: Parser.Context): STATEMENT
}

interface Statement : Node

class ExpressionStatement(val expression: Expression) : Statement


data class Block(val statements: List<Statement>, override val size: Int) : Statement, Collection<Statement> {
    constructor(vararg statements: Statement) : this(statements.toList())
    constructor(statements: List<Statement>) : this(statements, statements.size)

    companion object {
        val empty = Block(listOf())
    }

    override fun contains(element: Statement) = statements.contains(element)

    override fun containsAll(elements: Collection<Statement>) = statements.containsAll(elements)

    override fun isEmpty() = statements.isEmpty()

    override fun iterator(): Iterator<Statement> = statements.iterator()
}