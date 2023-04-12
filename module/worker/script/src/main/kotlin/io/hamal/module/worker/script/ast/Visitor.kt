package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.*

interface Visitor {
    fun visit(node: Identifier)

    fun visit(node: NumberLiteral)
    fun visit(node: StringLiteral)
    fun visit(node: TrueLiteral)
    fun visit(node: FalseLiteral)
    fun visit(node: NilLiteral)
}
