package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.FalseLiteral
import io.hamal.module.worker.script.ast.expr.NilLiteral
import io.hamal.module.worker.script.ast.expr.TrueLiteral
import io.hamal.module.worker.script.ast.stmt.GlobalAssignment

interface Visitor {
    fun visit(identifier: Identifier)

    fun visit(literal: TrueLiteral)
    fun visit(literal: FalseLiteral)
    fun visit(literal: NilLiteral)

    fun visit(statement: GlobalAssignment)
}