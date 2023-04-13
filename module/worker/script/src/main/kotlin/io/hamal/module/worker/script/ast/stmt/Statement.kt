package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Parser
import io.hamal.module.worker.script.ast.Statement

internal interface ParseStatement {
    operator fun invoke(ctx: Parser.Context): Statement
}

interface Statement {
}


class BlockStatement(val statements: List<Statement>, override val size: Int) : Statement, Collection<Statement> {
    constructor(statements: List<Statement>) : this(statements, statements.size)

    companion object {
        val empty = BlockStatement(listOf())
    }

    override fun contains(element: Statement) = statements.contains(element)

    override fun containsAll(elements: Collection<Statement>) = statements.containsAll(elements)

    override fun isEmpty() = statements.isEmpty()

    override fun iterator(): Iterator<Statement> = statements.iterator()
}