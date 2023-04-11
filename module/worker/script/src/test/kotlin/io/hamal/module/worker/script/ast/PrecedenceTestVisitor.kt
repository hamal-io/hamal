package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.literal.LiteralNil
import io.hamal.module.worker.script.token.Token

class PrecedenceTestVisitor : Visitor {
    private val builder = StringBuilder()

    override fun visit(node: ExpressionLiteral) {
        when (node) {
            is LiteralNil -> builder.appendTokenValue(node)
        }
    }

    private fun StringBuilder.appendTokenValue(node: Node) {
        appendValue(node.token)
    }

    private fun StringBuilder.appendValue(token: Token) {
        append('(')
        append(token.value.value)
        append(')')
    }

    override fun toString(): String {
        return builder.toString()
    }
}