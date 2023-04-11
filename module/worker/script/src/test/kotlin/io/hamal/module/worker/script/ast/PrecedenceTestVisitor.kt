package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.FalseLiteral
import io.hamal.module.worker.script.ast.expr.NilLiteral
import io.hamal.module.worker.script.ast.expr.TrueLiteral
import io.hamal.module.worker.script.ast.stmt.GlobalAssignment
import io.hamal.module.worker.script.value.Value

class PrecedenceTestVisitor : Visitor {
    private val builder = StringBuilder()

    override fun visit(identifier: Identifier) {
        appendValue(identifier.value)
    }

    override fun visit(literal: TrueLiteral) {
        appendValue(literal.value)
    }

    override fun visit(literal: FalseLiteral) {
        appendValue(literal.value)
    }

    override fun visit(literal: NilLiteral) {
        appendValue(literal.value)
    }

    override fun visit(statement: GlobalAssignment) {
        TODO("Not yet implemented")
    }


    private fun appendValue(value: Value) {
        builder.append('(')
        builder.append(value.toString())
        builder.append(')')
    }

    override fun toString(): String {
        return builder.toString()
    }
}