package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.ast.expr.Operator

interface Node



interface Expression : Node


data class PrefixExpression(
    val operator: Operator,
    val value: Expression
) : Expression {

}

data class InfixExpression(
    val lhs: Expression,
    val operator: Operator,
    val rhs: Expression
) : Expression

interface Statement : Node

class ExpressionStatement(val expression: Expression) : Statement

