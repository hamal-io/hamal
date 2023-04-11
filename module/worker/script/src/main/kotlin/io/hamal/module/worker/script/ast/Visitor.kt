package io.hamal.module.worker.script.ast

interface Visitor {
    fun visit(node: ExpressionLiteral)
}