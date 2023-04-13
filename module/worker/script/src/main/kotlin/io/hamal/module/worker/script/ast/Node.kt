package io.hamal.module.worker.script.ast

interface Node

enum class Operator(val value: String) {
    Plus("+"),
}

interface Expression : Node

interface LiteralExpression : Expression

abstract class PrefixExpression(
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

