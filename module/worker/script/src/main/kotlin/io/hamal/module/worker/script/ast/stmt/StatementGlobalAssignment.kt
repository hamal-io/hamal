package io.hamal.module.worker.script.ast.stmt

import io.hamal.module.worker.script.ast.Expression
import io.hamal.module.worker.script.ast.Identifier
import io.hamal.module.worker.script.ast.Statement
import io.hamal.module.worker.script.ast.Visitor
import io.hamal.module.worker.script.token.Token

class StatementGlobalAssignment(
    override val token: Token,
    val identifiers: List<Identifier>,
    val expressions: List<Expression>
) : Statement {
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }

}