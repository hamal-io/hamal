package io.hamal.script.ast.stmt

import io.hamal.script.ast.Expression
import io.hamal.script.ast.Statement
import io.hamal.script.ast.expr.Identifier

class Assignment(
    val identifiers: List<Identifier>,
    val expressions: List<Expression>
) : Statement {
    init {
        assert(identifiers.isNotEmpty())
        assert(identifiers.size == expressions.size)
    }
}