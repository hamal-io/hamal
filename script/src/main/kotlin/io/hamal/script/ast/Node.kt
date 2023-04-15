package io.hamal.script.ast

import io.hamal.script.ast.expr.Operator

interface Node



interface Expression : Node



data class InfixExpression(
    val lhs: Expression,
    val operator: Operator,
    val rhs: Expression
) : Expression

interface Statement : Node

class ExpressionStatement(val expression: Expression) : Statement

