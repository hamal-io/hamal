package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.value.Value

interface Node

enum class Operator(val value: String) {
    Plus("+"),
}

interface Expression : Node {
    fun accept(visitor: Visitor)
}

abstract class LiteralExpression(
    val value: Value
) : Expression

abstract class PrefixExpression(
    val operator: Operator,
    val value: Expression
) : Expression {

}

data class InfixExpression(
    val lhs: Expression,
    val operator: Operator,
    val rhs: Expression
) : Expression {
    // FIXME remove visitor
    override fun accept(visitor: Visitor) {
        TODO("Not yet implemented")
    }
}

interface Statement : Node

class StatementExpression(val expression: Expression) : Statement

