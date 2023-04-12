package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.*
import io.hamal.module.worker.script.value.Value

class PrecedenceTestVisitor : Visitor {
    private val builder = StringBuilder()

    override fun visit(node: Identifier) {
        appendString(node.value)
    }

    override fun visit(node: TrueLiteral) {
        appendValue(node.value)
    }

    override fun visit(node: FalseLiteral) {
        appendValue(node.value)
    }

    override fun visit(node: NilLiteral) {
        appendValue(node.value)
    }

    override fun visit(node: StringLiteral) {
        appendValue(node.value)
    }

    private fun appendString(value: String) {
        builder.append('(')
        builder.append(value)
        builder.append(')')
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