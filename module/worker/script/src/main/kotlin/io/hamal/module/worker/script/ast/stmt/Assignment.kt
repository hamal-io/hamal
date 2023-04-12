package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.expr.Identifier

class Assignment(
    val identifiers: List<Identifier>,
    val expressions: List<Expression>
) : Statement {
    init {
        assert(identifiers.isNotEmpty())
        assert(identifiers.size == expressions.size)
    }
}