package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.value.Value

interface Expression : Node {
}

abstract class ExpressionLiteral<VALUE : Value>(
    val value: VALUE
) : Expression

abstract class ExpressionUnary(
    val value: Expression
) : Expression {

}

abstract class ExpressionBinary(
    val lhs: Expression,
    val rhs: Expression
) : Expression {

}

