package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.value.Value

interface Node

interface Operator

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

abstract class InfixExpression(
    val lhs: Expression,
    val operator: Operator,
    val rhs: Expression
) : Expression

interface Statement : Node

class StatementExpression(val expression: Expression) : Statement

