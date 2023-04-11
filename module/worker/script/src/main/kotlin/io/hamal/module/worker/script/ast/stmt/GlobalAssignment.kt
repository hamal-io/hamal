package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Identifier
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.Visitor

class GlobalAssignment(
    val identifiers: List<Identifier>,
    val expressions: List<Expression>
) : Statement {
    init {
        require(identifiers.isNotEmpty())
        require(identifiers.size == expressions.size)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}