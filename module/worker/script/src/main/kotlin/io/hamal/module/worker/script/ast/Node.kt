package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token
import io.hamal.module.worker.script.value.Value


interface Node {
}

interface Operator {}

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
) : Expression {

}

interface Statement : Node

class StatementExpression(val expression: Expression) : Statement

internal interface ParsePrefixExpression<out EXPRESSION : Expression> {
    operator fun invoke(tokens: ArrayDeque<Token>): EXPRESSION
}

internal interface ParseInfixExpression<out EXPRESSION : Expression> {
    operator fun invoke(tokens: ArrayDeque<Token>, expression: Expression): EXPRESSION
}
