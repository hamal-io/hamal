package io.hamal.module.worker.script.ast

import io.hamal.module.worker.script.token.Token

interface Expression : Node {
}

abstract class ExpressionLiteral(
    override val token: Token
) : Expression {
    
}

abstract class ExpressionUnary(
    override val token: Token,
    val value: Expression
) : Expression {

}

abstract class ExpressionBinary(
    override val token: Token,
    val lhs: Expression,
    val rhs: Expression
) : Expression {

}

